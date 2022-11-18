package edu.uncc.hw08;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;
import edu.uncc.hw08.databinding.MyChatsListItemBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyChatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentMyChatsBinding binding;
    private FirebaseAuth mAuth;

    public MyChatsFragment() {
        // Required empty public constructor
    }


    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_my_chats, container, false);
        binding=FragmentMyChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         mAuth = FirebaseAuth.getInstance();
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });

        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mListener.redirectToNewChat();
            }
        });


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // getUserData();
              //  getUserChat();
                getUserChat1();
            }
        });

       // getUserChat();
        adapter = new UserAdapter(getActivity(), R.layout.fragment_my_chats, mUser );
        binding.listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        getActivity().setTitle("My Chats");
    }
    List<UserDetails>list=new ArrayList<>();


    void getUserChat(){
    mUserList.add("MmPb9vWpy8pTxGXOUDug");
    mUserList.add("Pt9fgMpGfikhqBt3sR8r");

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("chatConversion")
            .whereIn("receiverId", mUserList)
            .whereEqualTo("senderId",mAuth.getCurrentUser().getUid())
            .orderBy("ownerName")
            //.limit(1)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    mUser.clear();
                    list.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().size() > 0) {

                            UserDetails userDetails = new UserDetails();
                            userDetails.setUserName(mAuth.getCurrentUser().getDisplayName());
                            userDetails.setUserId(mAuth.getCurrentUser().getUid());
                            userDetails.setSenderId(document.getData().get("senderId").toString());
                            userDetails.setReceiverId(document.getData().get("receiverId").toString());
                            userDetails.setTextMessage(document.getData().get("text").toString());
                            userDetails.setOwnerName(document.getData().get("ownerName").toString());


                            userDetails.setCreatedDate(document.getData().get("createdAt").toString());
                            Log.d("Demo", document.getId() + " => " + document.getData());
                            mUser.add(userDetails);
                            if (list.isEmpty()){
                                list.add(userDetails)  ;
                            }else {

                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });


        }

    void getUserChat1(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contactUsers")
                //.limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        mUser.clear();
                        list.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().size() > 0) {
                                document.toObject(UserDetails.class);
                                ArrayList<UserDetails>   userDetails1= (ArrayList<UserDetails>) document.getData().get("conversation");
                                if (userDetails1!=null){

                                    Log.d("Demo", document.getId() + " => " + document.getData() +""+ userDetails1);
                                    if (userDetails1!=null && userDetails1.size() > 0) {
                                        Map<String, String> map = (Map<String, String>) userDetails1.get(userDetails1.size() - 1);

                                   /// Collections.sort(userDetails1, Collections.reverseOrder());
                                    final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                                    final UserDetails userDetails = mapper.convertValue(map, UserDetails.class);

                                    Log.d("Demo", document.getId() + " => " + document.getData());
                                    mUser.add(userDetails);
                                }

                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });


    }



    UserAdapter adapter;
    ArrayList<UserDetails> mUser = new ArrayList<>();
    ArrayList<String> mUserList = new ArrayList<>();

    class UserAdapter extends ArrayAdapter<UserDetails>{

        public UserAdapter(@NonNull Context context, int resource, @NonNull List<UserDetails> objects) {
            super(context, resource, objects);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView==null){
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.my_chats_list_item, parent, false);
                ViewHolder viewHolder=new ViewHolder();
                viewHolder.textViewMessageText=convertView.findViewById(R.id.textViewMsgText);
                viewHolder.textViewMessageDate=convertView.findViewById(R.id.textViewMsgOn);
                viewHolder.textViewMsgBy=convertView.findViewById(R.id.textViewMsgBy);
                convertView.setTag(viewHolder);
            }
            UserDetails item = getItem(position);

            ViewHolder viewHolder= (ViewHolder) convertView.getTag();


            viewHolder.textViewMessageText.setText(item.getTextMessage());
            viewHolder.textViewMessageDate.setText(item.getCreatedDate());
            viewHolder.textViewMsgBy.setText(item.getOwnerName());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   mListener.redirectToChat(item.getReceiverId(), item.getOwnerName());
                }
            });



            return  convertView;
        }

        public class ViewHolder{
            TextView textViewMessageText;
            TextView textViewMessageDate;
            TextView textViewMsgBy;
            //  TextView releaseDateTextView;


        }
    }

    void getUserData(){
        FirebaseFirestore.getInstance().collection("contactUsers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mUserList.clear();

                        for (QueryDocumentSnapshot doc: value) {
                            // UserDetails forum = doc.toObject(UserDetails.class);
                            // if (doc.getString("status").equals("online")){
                            UserDetails userDetails = new UserDetails();

                           // userDetails.setUserId();

                            mUserList.add(doc.getString("userId"));

                            //  }

                        }

                    }
                });
    }

    MyChatsFragmentListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (MyChatsFragmentListener) context;
    }

    interface MyChatsFragmentListener {
        void logout();
        void redirectToNewChat();
        void redirectToChat(String userId, String userName);

    }


}