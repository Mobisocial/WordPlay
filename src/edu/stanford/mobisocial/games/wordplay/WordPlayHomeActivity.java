package edu.stanford.mobisocial.games.wordplay;

import java.util.List;

import mobisocial.socialkit.Obj;
import mobisocial.socialkit.musubi.DbFeed;
import mobisocial.socialkit.musubi.DbIdentity;
import mobisocial.socialkit.musubi.DbObj;
import mobisocial.socialkit.musubi.Musubi;
import mobisocial.socialkit.musubi.Musubi.DbThing;
import mobisocial.socialkit.musubi.multiplayer.Multiplayer;
import mobisocial.socialkit.musubi.multiplayer.TurnBasedApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    static String[] sGameProjection = new String[] { DbObj.COL_ID, DbObj.COL_FEED_ID };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        findViewById(R.id.new_game).setOnClickListener(mNewGameListener);
        if (Musubi.isMusubiInstalled(this)) {
            mMusubi = Musubi.forIntent(this, getIntent());
            String selection = "type = ?";
            String[] selectionArgs = new String[] { WordPlayKickoffActivity.TYPE };
            String order = DbObj.COL_LAST_MODIFIED_TIMESTAMP + " desc";
            Cursor cursor = mMusubi.queryAppData(sGameProjection, selection, selectionArgs, order);
    
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
            long localId = cursor.getLong(0);
            Uri feedUri = Musubi.uriForItem(DbThing.FEED, cursor.getLong(1));

            JSONObject json;
            try {
                json = getLatestState(localId);
                if (json == null) {
                    Log.w(TAG, "no state for " + localId);
                    json = new JSONObject();
                }
            } catch (Exception e) {
                Log.w(TAG, "couldnt get game " + localId);
                return;
            }

            JSONArray members = json.optJSONArray(Multiplayer.OBJ_MEMBERSHIP);
            int turn = json.optInt("member_cursor");
            if (members == null || turn >= members.length()) {
                Log.w(TAG, "couldnt render " + localId);
                return;
            }
            String gameName = "#" + localId;

            DbIdentity potential = mMusubi.userForGlobalId(feedUri, members.optString(turn));
            boolean myTurn = potential.isOwned();
            //Log.d(TAG, "turn " + turn + " is " + myTurn + ", " + json);

            TextView tv = (TextView)view;
            StringBuilder text = new StringBuilder(100);
            int me = -1;
            for (int i = 0; i < members.length(); i++) {
                DbIdentity id = mMusubi.userForGlobalId(feedUri, members.optString(i));
                if (id != null) {
                    text.append(id.getName()).append(", ");
                    if (id.isOwned()) {
                        me = i;
                    }
                } else {
                    text.append("???, ");
                }
            }
            text.setLength(text.length() - 2);

            Log.d(TAG, json.toString());
            if (json.has("state")) {
                JSONObject state = json.optJSONObject("state");
                if (state != null) {
                    if (state.optBoolean("gameover")) {
                        text.append("\n").append(state.optString("lastmove"));
                    } else if (me != -1 && state.has("racks")) {
                        /*
                        JSONArray rack = state.optJSONArray("racks").optJSONArray(me);
                        if (rack != null) {
                            text.append("\n[");
                            int len = rack.length();
                            for (int i = 0; i < len; i++) {
                                text.append(rack.optString(i));
                                if (i < len-1) {
                                    text.append(", ");
                                } else {
                                    text.append("]");
                                }
                            }
                        }*/
                    }
                }
            }

            if (myTurn) {
                tv.setBackgroundColor(0xff666666);
            } else {
                tv.setBackgroundColor(Color.TRANSPARENT);
            }
            tv.setText(text.toString());
            tv.setTag(localId);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView tv = new TextView(context);
            tv.setTextSize(24);
            return tv;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            Uri objUri = Musubi.uriForItem(DbThing.OBJECT, (Long)view.getTag());
            Intent game = new Intent(Intent.ACTION_VIEW);
            game.setDataAndType(objUri, Musubi.mimeTypeFor(WordPlayKickoffActivity.TYPE));
            game.setClass(WordPlayHomeActivity.this, WordPlayActivity.class);
            startActivity(game);
        }
    }

    JSONObject getLatestState(long localId) throws JSONException {
        Uri uri = Musubi.uriForDir(DbThing.OBJECT);
        String[] projection = new String[] { DbObj.COL_JSON };
        String selection = DbObj.COL_PARENT_ID + "=? and type='appstate'";
        String[] selectionArgs = new String[] { Long.toString(localId) };
        String sortOrder = DbObj.COL_INT_KEY + " desc limit 1";
        Cursor c = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        try {
            if (c.moveToFirst()) {
                return new JSONObject(c.getString(0));
            } else {
                c.close();
                selection = DbObj.COL_ID + "=?";
                c = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
                if (c.moveToFirst()) {
                    return new JSONObject(c.getString(0));
                } else {
                    return null;
                }
            }
        } finally {
            c.close();
        }
    }
}
