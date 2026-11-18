package com.mypet.little.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mypet.little.MainActivity
import com.mypet.little.R
import com.mypet.little.characters.CharacterRegistry
import com.mypet.little.characters.Mood
import com.mypet.little.data.PetLogic
import com.mypet.little.data.PetStore
import kotlin.math.abs
import kotlin.random.Random

/** کمک‌کننده برای روشن/خاموش کردن پت شناور روی صفحه گوشی */
object FloatingPet {
    fun canDraw(ctx: Context): Boolean = Settings.canDrawOverlays(ctx)

    fun start(ctx: Context) {
        ContextCompat.startForegroundService(ctx, Intent(ctx, FloatingPetService::class.java))
    }

    fun stop(ctx: Context) {
        ctx.stopService(Intent(ctx, FloatingPetService::class.java))
    }

    fun requestPermission(ctx: Context) {
        val i = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    }
}

/**
 * پت کوچولو که روی همه برنامه‌ها و صفحه اصلی گوشی نمایش داده می‌شود؛
 * متحرک است، با انگشت جابه‌جا می‌شود، با یک ضربه حرف می‌زند و با لمس طولانی برنامه باز می‌شود.
 */
class FloatingPetService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var store: PetStore
    private var root: FrameLayout? = null
    private lateinit var img: ImageView
    private lateinit var bubble: TextView
    private lateinit var params: WindowManager.LayoutParams
    private var bob: ObjectAnimator? = null
    private val handler = Handler(Looper.getMainLooper())
    private var density = 1f
    private var lastMood: Mood? = null
    private var ticks = 0
    private var forcedPose: String? = null
    private var forcedUntil = 0L

    private val ticker = object : Runnable {
        override fun run() {
            refresh()
            handler.postDelayed(this, 2500)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            PetStore(this).floatingOn = false
            stopSelf()
            return START_NOT_STICKY
        }
        startInForeground()
        if (root == null) {
            if (!Settings.canDrawOverlays(this)) {
                stopSelf()
                return START_NOT_STICKY
            }
            createOverlay()
        }
        return START_STICKY
    }

    private fun startInForeground() {
        val channelId = "floating_pet"
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(
                NotificationChannel(channelId, "پت شناور", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), flags)
        val stopIntent = Intent(this, FloatingPetService::class.java).setAction(ACTION_STOP)
        val stop = PendingIntent.getService(this, 1, stopIntent, flags)
        val n: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("پت کوچولوی من روی صفحه است 🐾")
            .setContentText("برای باز کردن برنامه بزن")
            .setContentIntent(open)
            .addAction(0, "بستن پت شناور", stop)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, n)
        }
    }

    private fun createOverlay() {
        store = PetStore(this)
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        density = resources.displayMetrics.density
        val frame = FrameLayout(this)

        img = ImageView(this)
        img.scaleType = ImageView.ScaleType.FIT_CENTER
        val imgSize = (120 * density).toInt()
        frame.addView(
            img,
            FrameLayout.LayoutParams(imgSize, imgSize, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        )

        bubble = TextView(this)
        bubble.textSize = 13f
        bubble.setTextColor(Color.parseColor("#333333"))
        val bg = GradientDrawable()
        bg.setColor(Color.WHITE)
        bg.cornerRadius = 18 * density
        bg.setStroke((1 * density).toInt(), Color.parseColor("#DDDDDD"))
        bubble.background = bg
        val pad = (8 * density).toInt()
        bubble.setPadding(pad, pad / 2, pad, pad / 2)
        bubble.maxWidth = (170 * density).toInt()
        bubble.visibility = View.GONE
        frame.addView(
            bubble,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            )
        )

        val type = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            (150 * density).toInt(),
            (190 * density).toInt(),
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = store.overlayX
        params.y = store.overlayY

        frame.setOnTouchListener(object : View.OnTouchListener {
            var startX = 0
            var startY = 0
            var touchX = 0f
            var touchY = 0f
            var moved = false
            var downTime = 0L
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = params.x
                        startY = params.y
                        touchX = e.rawX
                        touchY = e.rawY
                        moved = false
                        downTime = System.currentTimeMillis()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (e.rawX - touchX).toInt()
                        val dy = (e.rawY - touchY).toInt()
                        if (abs(dx) > 12 || abs(dy) > 12) moved = true
                        if (moved) {
                            params.x = startX + dx
                            params.y = startY + dy
                            wm.updateViewLayout(frame, params)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (moved) {
                            store.overlayX = params.x
                            store.overlayY = params.y
                        } else if (System.currentTimeMillis() - downTime > 700) {
                            val i = Intent(this@FloatingPetService, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                        } else {
                            onTapped()
                        }
                    }
                }
                return true
            }
        })

        root = frame
        wm.addView(frame, params)

        bob = ObjectAnimator.ofFloat(img, "translationY", 0f, -10f * density).apply {
            duration = 700
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        handler.post(ticker)
    }

    private fun currentPet() = store.loadPets().getOrNull(store.activeSlot)
        ?: store.loadPets().firstOrNull { it != null }

    private fun onTapped() {
        val p = currentPet() ?: return
        val ch = CharacterRegistry.get(p.characterId)
        say(ch.tapLines.randomOrNull() ?: "😊")
        forcedPose = "happy"
        forcedUntil = System.currentTimeMillis() + 2500
        refresh()
    }

    private fun say(text: String) {
        bubble.text = text
        bubble.visibility = View.VISIBLE
        handler.removeCallbacks(hideBubble)
        handler.postDelayed(hideBubble, 3500)
    }

    private val hideBubble = Runnable { bubble.visibility = View.GONE }

    private fun refresh() {
        val raw = currentPet()
        if (raw == null) {
            img.setImageDrawable(null)
            return
        }
        val p = PetLogic.applyDecay(raw, System.currentTimeMillis())
        val ch = CharacterRegistry.get(p.characterId)
        val mood = PetLogic.mood(p)
        val tier = PetLogic.tier(p)
        ticks++

        val lp = img.layoutParams as FrameLayout.LayoutParams
        if (p.stage == 0) {
            img.setImageResource(R.drawable.p_egg)
            img.clearColorFilter()
            val s = (70 * density).toInt()
            lp.width = s
            lp.height = s
        } else {
            val scale = PetSprites.stageScale(p.stage)
            val size = (120 * density * scale).toInt()
            lp.width = size
            lp.height = size
            var pose = PetSprites.poseFor(mood, com.mypet.little.data.Reaction.NONE)
            val now = System.currentTimeMillis()
            if (forcedPose != null && now < forcedUntil) {
                pose = forcedPose!!
            } else if (mood == Mood.HAPPY && ticks % 6 == 0) {
                // گاهی برای زنده‌بودن، پوز بازی
                forcedPose = PetSprites.POSE_PLAY
                forcedUntil = now + 3500
                pose = forcedPose!!
            }
            val key = if (pose == "sit") PetSprites.stageKey(p.stage) else pose
            img.setImageResource(PetSprites.resId(this, ch.id, key))
            img.setColorFilter(
                ColorMatrixColorFilter(
                    ColorMatrix(PetSprites.matrix(PetSprites.hueOf(p.bodyColor), tier, mood == Mood.SICK))
                )
            )
        }
        img.layoutParams = lp

        bob?.duration = if (mood == Mood.SLEEPING) 1800 else 700

        if (mood != lastMood) {
            if (lastMood != null && mood != Mood.HAPPY) {
                say(ch.dialogues[mood]?.randomOrNull() ?: "")
            }
            lastMood = mood
        } else if (ticks % 12 == 0 && mood != Mood.SLEEPING && Random.nextBoolean()) {
            say(ch.dialogues[mood]?.randomOrNull() ?: "")
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        bob?.cancel()
        root?.let {
            try {
                wm.removeView(it)
            } catch (e: Exception) {
                // نمایش قبلاً حذف شده
            }
        }
        root = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.mypet.little.STOP_FLOATING"
        const val NOTIF_ID = 77
    }
}
