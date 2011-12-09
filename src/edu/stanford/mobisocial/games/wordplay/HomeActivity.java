package edu.stanford.mobisocial.games.wordplay;

import mobisocial.socialkit.musubi.DbFeed;
import mobisocial.socialkit.musubi.DbObj;
import mobisocial.socialkit.musubi.Musubi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {
    private Musubi mMusubi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (!Musubi.isMusubiInstalled(this)) {
            new AlertDialog.Builder(this).setTitle("Install Musubi?")
                .setMessage("This application lets you connect with friends using the Musubi app" +
            " platform. Would you like to install Musubi now?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent getMusubi = Musubi.getMarketIntent();
                    startActivity(getMusubi);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .create().show();
            return;
        }

        mMusubi = Musubi.getInstance(this);
        String[] projection = null;
        String selection = "type = ?";
        String[] selectionArgs = new String[] { "app" };
        String order = null;
        Cursor cursor = mMusubi.queryAppData(
                projection, selection, selectionArgs, order);

        ListView lv = (ListView)findViewById(R.id.gamelist);
        GameSummaryAdapter gsa = new GameSummaryAdapter(this, cursor);
        lv.setAdapter(gsa);
        lv.setOnItemClickListener(gsa);
    }

    class GameSummaryAdapter extends CursorAdapter implements OnItemClickListener {
        public GameSummaryAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            DbObj obj = mMusubi.objForCursor(cursor);
            TextView tv = (TextView)view;
            tv.setText("Game #" + (obj.getHash() % 10000));
            tv.setTag(obj);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView tv = new TextView(context);
            return tv;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            DbObj obj = (DbObj)view.getTag();
            Intent game = new Intent(Intent.ACTION_VIEW);
            game.setClass(HomeActivity.this, WordPlayActivity.class);
            game.putExtra(Musubi.EXTRA_FEED_URI, DbFeed.uriForName(obj.getFeedName()));
            game.putExtra(Musubi.EXTRA_OBJ_HASH, obj.getHash());
            startActivity(game);
        }
    }
}
