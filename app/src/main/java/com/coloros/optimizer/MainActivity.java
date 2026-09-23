package com.coloros.optimizer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private SharedPreferences prefs;
    private int currentMode = 2;
    
    private final String[] modeNames = {"极限降温", "省电模式", "均衡模式", "性能模式", "狂暴模式"};
    private final int[] modeColors = {0xFF00D4FF, 0xFF34C759, 0xFFFF9500, 0xFFFF3B30, 0xFFAF52DE};
    private final String[] modeDescriptions = {
        "强制降频，温度优先",
        "延长续航，限制后台",
        "平衡性能与续航",
        "提升性能，适当发热",
        "解除温控，性能拉满"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // iOS 风格状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#F5F5F7"));
            window.setNavigationBarColor(Color.parseColor("#F5F5F7"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        setContentView(R.layout.activity_main);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentMode = prefs.getInt("mode", 2);
        HookModule.currentMode = currentMode;

        createGlassCard();
        createModeButtons();
        updateStatus();
    }

    private void createGlassCard() {
        FrameLayout container = findViewById(R.id.cardContainer);
        
        // 液态玻璃卡片
        LinearLayout glassCard = new LinearLayout(this);
        glassCard.setOrientation(LinearLayout.VERTICAL);
        glassCard.setGravity(Gravity.CENTER);
        glassCard.setPadding(32, 40, 32, 40);
        
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setCornerRadius(dp(28));
        cardBg.setColor(Color.parseColor("#80FFFFFF"));
        cardBg.setStroke(dp(1), Color.parseColor("#30FFFFFF"));
        glassCard.setBackground(cardBg);
        
        // 模式指示器圆
        View indicator = new View(this);
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(dp(80), dp(80));
        indicatorParams.gravity = Gravity.CENTER;
        indicator.setLayoutParams(indicatorParams);
        
        GradientDrawable indicatorBg = new GradientDrawable();
        indicatorBg.setShape(GradientDrawable.OVAL);
        indicatorBg.setColor(modeColors[currentMode]);
        indicator.setBackground(indicatorBg);
        
        // 动画效果
        animateIndicator(indicator);
        
        // 当前模式名称
        TextView modeLabel = new TextView(this);
        modeLabel.setText(modeNames[currentMode]);
        modeLabel.setTextSize(28);
        modeLabel.setTextColor(Color.parseColor("#1C1C1E"));
        modeLabel.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        modeLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.topMargin = dp(20);
        labelParams.gravity = Gravity.CENTER;
        modeLabel.setLayoutParams(labelParams);
        modeLabel.setTag("modeLabel");
        
        // 模式描述
        TextView descLabel = new TextView(this);
        descLabel.setText(modeDescriptions[currentMode]);
        descLabel.setTextSize(14);
        descLabel.setTextColor(Color.parseColor("#8E8E93"));
        descLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        descParams.topMargin = dp(8);
        descParams.gravity = Gravity.CENTER;
        descLabel.setLayoutParams(descParams);
        descLabel.setTag("descLabel");
        
        glassCard.addView(indicator);
        glassCard.addView(modeLabel);
        glassCard.addView(descLabel);
        container.addView(glassCard);
    }

    private void animateIndicator(View indicator) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(indicator, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(indicator, "scaleY", 1f, 1.1f, 1f);
        scaleX.setDuration(1500);
        scaleY.setDuration(1500);
        
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(scaleX, scaleY);
        animSet.start();
        
        // 循环动画
        indicator.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (indicator.isShown()) {
                    animateIndicator(indicator);
                }
            }
        }, 1500);
    }

    private void createModeButtons() {
        LinearLayout container = findViewById(R.id.modeContainer);
        container.removeAllViews();

        for (int i = 0; i < 5; i++) {
            final int mode = i;
            
            LinearLayout button = new LinearLayout(this);
            button.setOrientation(LinearLayout.HORIZONTAL);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setPadding(dp(20), dp(16), dp(20), dp(16));
            
            GradientDrawable btnBg = new GradientDrawable();
            btnBg.setCornerRadius(dp(16));
            btnBg.setColor(Color.parseColor("#FFFFFFFF"));
            btnBg.setStroke(dp(1), Color.parseColor("#20000000"));
            button.setBackground(btnBg);
            
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            btnParams.topMargin = dp(8);
            button.setLayoutParams(btnParams);
            
            // 颜色指示条
            View colorBar = new View(this);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dp(4), dp(32));
            barParams.rightMargin = dp(16);
            colorBar.setLayoutParams(barParams);
            
            GradientDrawable barBg = new GradientDrawable();
            barBg.setCornerRadius(dp(2));
            barBg.setColor(modeColors[i]);
            colorBar.setBackground(barBg);
            
            // 文字区域
            LinearLayout textArea = new LinearLayout(this);
            textArea.setOrientation(LinearLayout.VERTICAL);
            
            TextView nameText = new TextView(this);
            nameText.setText(modeNames[i]);
            nameText.setTextSize(16);
            nameText.setTextColor(Color.parseColor("#1C1C1E"));
            nameText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            
            TextView descText = new TextView(this);
            descText.setText(modeDescriptions[i]);
            descText.setTextSize(12);
            descText.setTextColor(Color.parseColor("#8E8E93"));
            descText.setPadding(0, dp(4), 0, 0);
            
            textArea.addView(nameText);
            textArea.addView(descText);
            
            // 选中指示
            TextView checkMark = new TextView(this);
            checkMark.setText("✓");
            checkMark.setTextSize(20);
            checkMark.setTextColor(modeColors[i]);
            checkMark.setVisibility(i == currentMode ? View.VISIBLE : View.INVISIBLE);
            checkMark.setTag("check_" + i);
            
            LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            );
            checkMark.setLayoutParams(checkParams);
            checkMark.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            
            button.addView(colorBar);
            button.addView(textArea);
            button.addView(checkMark);
            
            button.setOnClickListener(v -> {
                switchMode(mode);
                container.removeAllViews();
                createModeButtons();
            });
            
            container.addView(button);
        }
    }

    private void switchMode(int mode) {
        currentMode = mode;
        HookModule.currentMode = mode;
        prefs.edit().putInt("mode", mode).apply();
        updateStatus();
        
        // 更新卡片
        FrameLayout container = findViewById(R.id.cardContainer);
        container.removeAllViews();
        createGlassCard();
        
        // 显示提示
        Toast.makeText(this, "已切换至: " + modeNames[mode], Toast.LENGTH_SHORT).show();
    }

    private void updateStatus() {
        TextView status = findViewById(R.id.statusText);
        status.setText("当前模式: " + modeNames[currentMode] + " | ColorOS 16 已激活");
    }

    private int dp(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
