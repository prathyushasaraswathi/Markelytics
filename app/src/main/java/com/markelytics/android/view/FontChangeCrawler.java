package com.markelytics.android.view;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FontChangeCrawler
{
    private Typeface normalFont;
    private Typeface boldFont;
    private Typeface italicFont;
    private Typeface regularFont;

    AssetManager asset;

   

    public FontChangeCrawler(AssetManager assets)
    {
    	asset = assets;
    }

    public void replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child);
            }
            else if(child instanceof TextView)
            {
                // base case
            	if(((TextView) child).getTypeface() != null && ((TextView) child).getTypeface().isBold()) {
            		 ((TextView) child).setTypeface(getBoldFont());
                 }/* else if(((TextView) child).getTypeface() != null && ((TextView) child).getTypeface().isItalic()) {
            		 ((TextView) child).setTypeface(getItalicFont());
                 } *//*else if(((TextView) child).getTypeface() != null && ((TextView) child).getTypeface().isLight) {
                     ((TextView) child).setTypeface(getLightFont());*//*

                 else {*/
                	 ((TextView) child).setTypeface(getRegularFont());
                 //}
            }else if(child instanceof EditText)
            {
                // base case
            	 if(((EditText) child).getTypeface() != null && ((EditText) child).getTypeface().isBold()) {
            		 ((EditText) child).setTypeface(getBoldFont());
                 }/*else if(((EditText) child).getTypeface() != null && ((EditText) child).getTypeface().isItalic()) {
            		 ((EditText) child).setTypeface(getItalicFont());
                 }*//*else if(((EditText) child).getTypeface() != null && ((EditText) child).getTypeface().isLight()) {
                     ((EditText) child).setTypeface(getLightFont());
                 } */ //else {
                	 ((EditText) child).setTypeface(getRegularFont());
                 //}
            }else if(child instanceof Button)
            {
                // base case
            	if(((Button) child).getTypeface() != null && ((Button) child).getTypeface().isBold()) {
            		 ((Button) child).setTypeface(getBoldFont());
                 } /*else if(((Button) child).getTypeface() != null && ((Button) child).getTypeface().isItalic()) {
            		 ((Button) child).setTypeface(getItalicFont());
                 }*//*else if(((Button) child).getTypeface() != null && ((Button) child).getTypeface().isLight()) {
                     ((Button) child).setTypeface(getLightFont());
                 }  *//*else {*/
                	 ((Button) child).setTypeface(getRegularFont());
                 //}
            }
        }
    }
   
   /* private Typeface getNormalFont() {
        if(normalFont == null) {
            normalFont = Typeface.createFromAsset(asset,"OpenSansRegular.ttf");
        }
        return this.normalFont;
    }*/
    private Typeface getBoldFont() {
        if(boldFont == null) {
            boldFont = Typeface.createFromAsset(asset,"Lato-Bold.ttf");
        }
        return this.boldFont;
    }
    
    /* private Typeface getItalicFont() {
        if(italicFont == null) {
        	italicFont = Typeface.createFromAsset(asset,"OpenSansItalic.ttf");
        }
        return this.italicFont;
    }*/

    private Typeface getRegularFont() {
        if(regularFont == null) {
            regularFont = Typeface.createFromAsset(asset,"Lato-Regular.ttf");
        }
        return this.regularFont;
    }
}
