package cn.ucai.fulicenter.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by wdyzyr on 2016/8/3.
 */
public class DisplyUtils {
    public static void initBack(final Activity activity){
        activity.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public static void initTitle(final Activity activity,final String title){
        initBack(activity);
        ((TextView)activity.findViewById(R.id.tvTitle)).setText(title);
    }
    public static void initTitle(final Activity activity){
        initBack(activity);
        ((TextView)activity.findViewById(R.id.tvTitle)).setText("");
    }
}
