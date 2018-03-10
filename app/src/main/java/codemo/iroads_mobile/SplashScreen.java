package codemo.iroads_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView welcome;

    Animation level1;
    Animation level2;
    Animation level3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        welcome =(ImageView) findViewById(R.id.welcome);
        welcome.setImageResource(R.mipmap.ic_phone);

        level1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.level1);
        level2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.level2);
        level3 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.level3);
//        final Animation fade = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

//        welcome.setVisibility(View.VISIBLE);
        welcome.startAnimation(level1);
//        welcome.startAnimation(level2);
        level1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Toast.makeText(getApplicationContext(), "welcome..", Toast.LENGTH_SHORT).show();
//                welcome.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                welcome.setImageResource(R.mipmap.ic_car);
                welcome.startAnimation(level2);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        level2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                welcome.setImageResource(R.mipmap.ic_iroads);
                welcome.startAnimation(level3);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        level3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}