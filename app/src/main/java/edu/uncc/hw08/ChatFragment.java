package edu.uncc.hw08;

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
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;


public class ChatFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    FragmentChatBinding binding;


    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Chat "+ mParam2);
        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.redirectToMyChats();
            }
        });


            binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String message = binding.editTextMessage.getText().toString();

                    if (message.isEmpty()) {
                        Toast.makeText(getActivity(), "Enter valid msg!", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.editTextMessage.setText("");
                        UserDetails user = new UserDetails();
                        HashMap<String, Object> data = new HashMap<>();
                        user.setSenderId(mAuth.getCurrentUser().getUid());
                        user.setReceiverId(mParam1);
                        user.setUserId(mParam1);
                        user.setUserName(mParam2);
                        user.setOwnerName(mParam2);
                        user.setTextMessage(message);
                        user.setOwnerId(mAuth.getCurrentUser().getUid());
                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
                        Date date = new Date(System.currentTimeMillis());
                        System.out.println(formatter.format(date));

                        user.setCreatedDate(formatter.format(date));

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        DocumentReference docRef1 =db.collection("contactUsers").document(mParam1);
                      //  data.put("conversationId", docRef1.getId());
                        user.setConversationId(docRef1.getId());
                        data.put("conversation", FieldValue.arrayUnion(user));
                        docRef1.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // mListener.createForumDone();
                                    adapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(getActivity(), "Error creating chat!!", Toast.LENGTH_SHORT).show();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                }
            });




        getUserChat1();


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatsFragmentAdapter();
        binding.recyclerView.setAdapter(adapter);

        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore.getInstance().collection("chatConversion").document(mParam1)

                        .delete()

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("demo", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("demo", "Error deleting document", e);
                            }
                        });
            }
        });



    }

    void getData(){

        FirebaseFirestore.getInstance().collection("chatConversion")
                .whereEqualTo("receiverId", mParam1)
                .whereEqualTo("senderId",mAuth.getCurrentUser().getUid())
                //.orderBy("created_at")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                      //  mComments.clear();
                        mUserList.clear();
                        if (e != null) {
                            Log.w("demo", "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc: value) {
                            /*Comment comment = doc.toObject(Comment.class);
                            mComments.add(comment);*/
                            UserDetails userDetails = new UserDetails();
                          //  userDetails.setConversationId(doc.getString("conversationId"));
                            userDetails.setCreatedDate(doc.getString("createdAt"));
                            //  userDetails.set(doc.getString("ownerId"));
                            userDetails.setUserName(doc.getString("ownerName"));
                            userDetails.setReceiverId(doc.getString("receiverId"));
                            userDetails.setSenderId(doc.getString("senderId"));
                            userDetails.setTextMessage(doc.getString("text"));
                            mUserList.add(userDetails);

                        }
                        adapter.notifyDataSetChanged();
                       // commentsAdapter.notifyDataSetChanged();

                       // binding.textViewCommentsCount.setText(mComments.size() + " Comments");

                    }
                });
    }

    void getUserChat1(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contactUsers")
                .whereEqualTo("userId", mParam1)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        //  mComments.clear();
                        mUserList.clear();
                        if (e != null) {
                            Log.w("demo", "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot document: value) {

                            ArrayList<UserDetails>   userDetails1= (ArrayList<UserDetails>) document.getData().get("conversation");

                            for(int i=0;i<userDetails1.size();i++){
                                if (userDetails1!=null){

                                    Log.d("Demo", document.getId() + " => " + document.getData() +""+ userDetails1);
                                    if (userDetails1!=null && userDetails1.size() > 0) {
                                        Map<String, String> map = (Map<String, String>) userDetails1.get(i);

                                        /// Collections.sort(userDetails1, Collections.reverseOrder());
                                        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                                        final UserDetails userDetails = mapper.convertValue(map, UserDetails.class);

                                        Log.d("Demo", document.getId() + " => " + document.getData());
                                        mUserList.add(userDetails);
                                    }

                                }
                            }


                        }
                        adapter.notifyDataSetChanged();

                    }
                });

    }



    ChatsFragmentAdapter adapter;
    ArrayList<UserDetails> mUserList = new ArrayList<>();

    class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.PostsViewHolder> {
        @NonNull
        @Override
        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
           // MyChatsListItemBinding binding = MyChatsListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new PostsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
            UserDetails userDetails = mUserList.get(position);
            holder.setupUI(userDetails);
        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }

        class PostsViewHolder extends RecyclerView.ViewHolder {
            ChatListItemBinding mBinding;
            UserDetails mUser;
            public PostsViewHolder(ChatListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(UserDetails user) {
                mUser = user;
               // mBinding.textViewMsgBy.setText(user.getUserName());
                mBinding.textViewMsgText.setText(mUser.getTextMessage());
                mBinding.textViewMsgOn.setText(mUser.getCreatedDate());

                if (mUser.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                    mBinding.textViewMsgBy.setText("Me");
                    mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HashMap<String, Object> data = new HashMap<>();
                            //mUserList.remove(mUser);
                            data.put("conversation", FieldValue.arrayRemove(mUser));

                          // FirebaseFirestore.getInstance().collection("contactUsers").document(mUser.getConversationId()).delete();
                            FirebaseFirestore.getInstance().collection("contactUsers")
                                    .document(mParam1).update(data).addOnCompleteListener(new OnCompleteListener<Void>() { //mParam1   mUser.getReceiverId()
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });



                            adapter.notifyDataSetChanged();
                        }
                    });
                }else {
                    mBinding.textViewMsgBy.setText(mUser.getUserName());
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }
    ChatsFragmentListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ChatsFragmentListener) context;
    }

    interface ChatsFragmentListener {

        void redirectToMyChats();

    }


}