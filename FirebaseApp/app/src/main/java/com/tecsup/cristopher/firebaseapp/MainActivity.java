package com.tecsup.cristopher.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("fullname", "Cristopher Torres");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        mFirebaseAnalytics.setUserProperty("username", "ctorres");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        User user = new User();
        user.setUid(currentUser.getUid());
        user.setDisplayName(currentUser.getDisplayName());
        user.setEmail(currentUser.getEmail());
        user.setPhotoUrl((currentUser.getPhotoUrl()!=null?currentUser.getPhotoUrl().toString():null));

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange " + dataSnapshot.getKey());

                User user = dataSnapshot.getValue(User.class);
                setTitle(user.getDisplayName());
            }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
                }

            });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                callLogout(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callLogout(View view) {
        Log.d(TAG, "Ssign out user");
        FirebaseAuth.getInstance().signOut();
        finish();
    }
    private static final int REGISTER_FORM_REQUEST = 100;

    public void showRegister(View view){
        startActivityForResult(new Intent(this, RegisterActivity.class), REGISTER_FORM_REQUEST);
    }
}
