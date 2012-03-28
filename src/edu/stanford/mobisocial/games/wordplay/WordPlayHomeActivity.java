package edu.stanford.mobisocial.games.wordplay;

import mobisocial.socialkit.musubi.DbIdentity;
import mobisocial.socialkit.musubi.DbObj;
import mobisocial.socialkit.musubi.Musubi;
import mobisocial.socialkit.musubi.multiplayer.FeedRenderable;
import mobisocial.socialkit.musubi.multiplayer.TurnBasedApp;

import org.json.JSONObject;

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

public class WordPlayHomeActivity extends Activity {
    static final String TAG = "WordPlayHome";
    private Musubi mMusubi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (!Musubi.isMusubiInstalled(this)) {
            new AlertDialog.Builder(this).setTitle("Install Musubi?")
                .setMessage("WordPlay lets you play with friends using the Musubi app" +
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

        mMusubi = Musubi.forIntent(this, getIntent());
        String[] projection = null;
        String selection = "type = ?";
        String[] selectionArgs = new String[] { WordPlayKickoffActivity.TYPE };
        String order = DbObj.COL_LAST_MODIFIED_TIMESTAMP + " desc";
        Cursor cursor = mMusubi.queryAppData(projection, selection, selectionArgs, order);

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
            WordPlayApp app = new WordPlayApp(obj);
            String[] members = app.getMembers();

            TextView tv = (TextView)view;
            StringBuilder text = new StringBuilder(100)
                .append("Game #" + Math.abs(obj.getHash() % 1000)).append(": ");
            for (int i = 0; i < members.length; i++) {
                DbIdentity id = mMusubi.userForGlobalId(obj.getContainingFeed().getUri(),
                        members[i]);
                if (id != null) {
                    text.append(id.getName()).append(", ");
                } else {
                    text.append("???, ");
                }
            }
            text.setLength(text.length() - 2);
            if (app.isMyTurn()) {
                text.append(". Your turn.");
            }
            tv.setText(text.toString());
            tv.setTag(obj);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView tv = new TextView(context);
            tv.setTextSize(20);
            return tv;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            DbObj obj = (DbObj)view.getTag();
            Intent game = new Intent(Intent.ACTION_VIEW);
            game.setDataAndType(obj.getUri(), Musubi.mimeTypeFor(obj.getType()));
            game.setClass(WordPlayHomeActivity.this, WordPlayActivity.class);
            game.putExtra(Musubi.EXTRA_FEED_URI, obj.getContainingFeed().getUri());
            startActivity(game);
        }
    }

    /**
     * Stub to work around the fact that WordPlay's TurnBasedApp is not static
     * and TBA is abstract.
     */
    class WordPlayApp extends TurnBasedApp {
        public WordPlayApp(DbObj objContext) {
            super(objContext);
        }

        @Override
        protected FeedRenderable getFeedView(JSONObject arg0) {
            return null;
        }

        @Override
        protected void onStateUpdate(JSONObject arg0) {

        }
    }
}
