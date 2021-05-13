package fastcampus.aop.part3.chapter06.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter06.DBKey.Companion.CHILD_CHAT
import fastcampus.aop.part3.chapter06.DBKey.Companion.DB_USERS
import fastcampus.aop.part3.chapter06.R
import fastcampus.aop.part3.chapter06.chatdetail.ChatRoomActivity
import fastcampus.aop.part3.chapter06.databinding.FragmentChatlistBinding
import fastcampus.aop.part3.chapter06.home.ArticleAdapter

class ChatListFragment: Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentchatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentchatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom->
            //채팅방으로 이동하는 코드
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }
        })

        chatRoomList.clear()

        fragmentchatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentchatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if(auth.currentUser == null) {
            return
        }

        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser.uid).child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }



        })

    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}