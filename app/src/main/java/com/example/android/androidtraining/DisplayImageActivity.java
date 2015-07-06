// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

// Referenced classes of package com.example.android.androidtraining:
//            DisplayMessageActivity

public class DisplayImageActivity extends ActionBarActivity
{

    private ShareActionProvider mShareActionProvider;

    public DisplayImageActivity()
    {
    }

    private Intent getDefaultIntent()
    {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse((new StringBuilder()).append("android.resource://").append(getPackageName()).append("/").append(R.drawable.prairiedogs).append(".jpg").toString()));
        return intent;
    }

    public Intent getSupportParentActivityIntent()
    {
        return new Intent(this, DisplayMessageActivity.class);
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        getSupportActionBar();
        setContentView(R.layout.activity_display_image);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
        mShareActionProvider.setShareIntent(getDefaultIntent());
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        if (menuitem.getItemId() == R.id.action_settings)
        {
            return true;
        } else
        {
            return super.onOptionsItemSelected(menuitem);
        }
    }
}
