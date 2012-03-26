package edu.stanford.mobisocial.games.wordplay;

import java.util.List;

import mobisocial.socialkit.Obj;
import mobisocial.socialkit.musubi.DbIdentity;
import mobisocial.socialkit.musubi.Musubi;
import mobisocial.socialkit.musubi.multiplayer.TurnBasedApp;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class WordPlayKickoffActivity extends Activity {
    final String TYPE = "wordplay";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Musubi m = Musubi.forIntent(this, getIntent());
        List<DbIdentity> members = m.getFeed().getMembers();
        if (members.size() > 4) {
            members = members.subList(0, 4);
        }
        JSONObject initialState = WordPlayActivity.getInitialState(members.size());
        if (members.size() < 2) {
            Toast.makeText(this, "Not enough players for WordPlay", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<DbIdentity> players = members.subList(0, 2);        
        Obj game = TurnBasedApp.newInstance(TYPE, players, initialState);
        Uri objUri = m.getFeed().insert(game);
        Intent view = new Intent(Intent.ACTION_VIEW);
        view.setDataAndType(objUri, Musubi.mimeTypeFor(TYPE));
        startActivity(view);
        finish();
    }
}
