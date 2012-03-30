package edu.stanford.mobisocial.games.wordplay;

import java.util.List;

import mobisocial.socialkit.Obj;
import mobisocial.socialkit.musubi.DbFeed;
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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WordPlayHomeActivity extends Activity {
    static final String TAG = "WordPlayHome";
    private Musubi mMusubi;
    static final String ACTION_CREATE_FEED = "musubi.intent.action.CREATE_FEED";
    static final int REQUEST_CREATE_FEED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        findViewById(R.id.new_game).setOnClickListener(mNewGameListener);
        if (Musubi.isMusubiInstalled(this)) {
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
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CREATE_FEED) {
            if (resultCode == RESULT_OK) {
                Uri feedUri = data.getData();
                DbFeed feed = mMusubi.getFeed(feedUri);

                List<DbIdentity> players = feed.getMembers();
                if (players.size() > 4) {
                    players = players.subList(0, 4);
                }
                JSONObject initialState = WordPlayActivity.getInitialState(players.size());
                if (players.size() < 2) {
                    Toast.makeText(this, "Not enough players for WordPlay", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Obj game = TurnBasedApp.newInstance(WordPlayKickoffActivity.TYPE, players, initialState);
                Uri objUri = feed.insert(game);
                Intent view = new Intent(Intent.ACTION_VIEW);
                view.setDataAndType(objUri, Musubi.mimeTypeFor(game.getType()));
                startActivity(view);
                finish();
            }
        }
    };

    OnClickListener mNewGameListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!Musubi.isMusubiInstalled(WordPlayHomeActivity.this)) {
                new AlertDialog.Builder(WordPlayHomeActivity.this).setTitle("Install Musubi?")
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

            Intent create = new Intent(ACTION_CREATE_FEED);
            startActivityForResult(create, REQUEST_CREATE_FEED);
        }
    };

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
            tv.setTextSize(24);
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
