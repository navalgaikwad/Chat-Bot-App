package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;
import edu.uncc.hw08.databinding.FragmentMyChatsBinding;


public class CreateChatFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    FragmentCreateChatBinding binding;

    public CreateChatFragment() {
        // Required empty public constructor
    }


    public static CreateChatFragment newInstance(String param1, String param2) {
        CreateChatFragment fragment = new CreateChatFragment();
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
        getActivity().setTitle("New Chats");
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_create_chat, container, false);
        binding= FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    boolean isSelected = false;
    UserDetails user = new UserDetails();
    String usernameId = "";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HashMap<String, Object> data = new HashMap<>();
        //b
        mAuth = FirebaseAuth.getInstance();
      /*  adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, list);
        binding.listView.setAdapter(adapter);*/
        getData();
        adapter = new UserAdapter(getActivity(), R.layout.fragment_create_chat, mUser );
        binding.listView.setAdapter(adapter);
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                isSelected = true;
                binding.textViewSelectedUser.setText(mUser.get(i).getUserName());
                UserDetails item = adapter.getItem(i);
                //UserDetails user = new UserDetails();
                user.setSenderId(mAuth.getCurrentUser().getUid());
             //   data.put("senderId", mAuth.getCurrentUser().getUid());
                user.setReceiverId(item.getUserId());
                user.setUserId(item.getUserId());
                user.setUserName(item.getUserName());
             //   data.put("receiverId", item.getUserId());
                user.setOwnerName(item.getUserName());
                usernameId= item.getUserId();
              //  data.put("ownerName", item.getUserName());
                user.setOwnerId(mAuth.getCurrentUser().getUid());
            //    data.put("ownerId", mAuth.getCurrentUser().getUid());
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    System.out.println(formatter.format(date));

              //  data.put("createdAt", formatter.format(date));
                user.setCreatedDate(formatter.format(date));

               // data.put("likes", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
               // data.put("likes", new ArrayList<String>());
            }
        });
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSelected){
                    Toast.makeText(getContext(), "Please select a user", Toast.LENGTH_SHORT).show();
                    return;
                }
                String text = binding.editTextMessage.getText().toString();
               // data.put("text", text);
                user.setTextMessage(text);
                data.put("conversation", FieldValue.arrayUnion(user));

                binding.editTextMessage.setText("");


                FirebaseFirestore.getInstance().collection("contactUsers")
                        .document(usernameId).update(data);
                isSelected = false;
                mListener.redirectToMyChatFragment();

               /* FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("chatConversion")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                isSelected = false;
                                mListener.redirectToMyChatFragment();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Demo", "Error adding document", e);
                                Log.w("TAG", "Error creating user :failure"+ e.getMessage());
                                Toast.makeText(getActivity(), "Error creating user failed.", Toast.LENGTH_SHORT).show();
                            }
                        });*/

            /*    DocumentReference docRef = db.collection("chatConversion").document(mAuth.getCurrentUser().getUid());
                data.put("conversationId", docRef.getId());

                docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            isSelected = false;
                            mListener.redirectToMyChatFragment();
                        } else {
                            Toast.makeText(getActivity(), "Error creating chat!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

            }
        });
    }
    void getData(){
        FirebaseFirestore.getInstance().collection("contactUsers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mUser.clear();

                for (QueryDocumentSnapshot doc: value) {
                   // UserDetails forum = doc.toObject(UserDetails.class);
                    if (doc.getString("status").equals("online")){
                        UserDetails userDetails = new UserDetails();
                        userDetails.setUserName(doc.getString("userName"));
                        userDetails.setUserId(doc.getString("userId"));
                        userDetails.setStatus(doc.getString("status"));
                        mUser.add(userDetails);
                    }

                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    UserAdapter adapter;
    ArrayList<UserDetails> mUser = new ArrayList<>();

    class UserAdapter extends ArrayAdapter<UserDetails> {

        public UserAdapter(@NonNull Context context, int resource, @NonNull List<UserDetails> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView==null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_row_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
                convertView.setTag(viewHolder);
            }

            UserDetails item = getItem(position);
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();


                viewHolder.textViewName.setText(item.getUserName());


            return  convertView;
        }

        public class ViewHolder{
            TextView textViewName;
            //  TextView releaseDateTextView;
        }
    }





    CreateChatsFragmentListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateChatsFragmentListener) context;
    }

    interface CreateChatsFragmentListener {
        void logout();
        void redirectToMyChatFragment();
    }
}