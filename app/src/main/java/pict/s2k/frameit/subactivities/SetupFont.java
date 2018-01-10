package pict.s2k.frameit.subactivities;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by kshitij on 9/9/17.
 */

public class SetupFont {
    public static void setupDefaultFont(Activity activity){
        Calligrapher calligrapher=new Calligrapher(activity);
        calligrapher.setFont(activity,"fonts/Quicksand-Regular.ttf",true);
    }
    public static void setupFontForView(View view){
        Calligrapher calligrapher=new Calligrapher(view.getContext());
        calligrapher.setFont(view,"fonts/Quicksand-Regular.ttf");
    }
}
