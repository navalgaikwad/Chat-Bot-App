package edu.uncc.hw08;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements MyChatsFragment.MyChatsFragmentListener,
        CreateChatFragment.CreateChatsFragmentListener, ChatFragment.ChatsFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new MyChatsFragment())
                .commit();
    }

    @Override
    public void logout() {
        getSupportFragmentManager().popBackStack();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void redirectToMyChatFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new MyChatsFragment())
                .commit();
    }

    @Override
    public void redirectToNewChat() {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.rootView, new CreateChatFragment())
                .commit();
    }

    @Override
    public void redirectToChat(String userId, String userName) {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.rootView, ChatFragment.newInstance(userId, userName))
                .commit();
    }
    @Override
    public void redirectToMyChats() {
        getSupportFragmentManager().popBackStack();
    }

/*    void getUserData(){
        FirebaseFirestore.getInstance().collection("contactUsers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mUserList.clear();

                        for (QueryDocumentSnapshot doc: value) {
                            // UserDetails forum = doc.toObject(UserDetails.class);
                            // if (doc.getString("status").equals("online")){
                            UserDetails userDetails = new UserDetails();
                            userDetails.setUserName(doc.getString("userName"));
                            userDetails.setUserId(doc.getString("userId"));
                            userDetails.setStatus(doc.getString("status"));
                            mUserList.add(userDetails);

                            //  }

                        }

                    }
                });
    }*/
}