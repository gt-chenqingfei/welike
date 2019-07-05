package com.redefine.demo

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.im.engine.IMEngine
import com.redefine.im.room.*
import com.redefine.im.service.socket.protocol.BibiProtoApplication
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: ChatViewModel
    val user = "110404"
    val token = "2a0786ca2c33acde"
    val target = "150397"

//    val user = "150397"
//    val target = "110404"
//    val token = "454580d659fb028f"

    val engine by lazy {
        IMEngine.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        val adapter = MyAdapter(layoutInflater)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        button.text = "Connect"
        button2.text = "Messages"
        button3.text = "Sessions"
        button4.text = "Send"

        button5.text = "M"
        button6.text = "x M"
        button7.text = ""
        button8.text = "x Account"

        val x = newSESSION().copy(sid = "XXX")
        button.setOnClickListener {
            thread {
                engine.start(user, token, "en")
            }
        }
        button2.setOnClickListener {
//            thread { d("a = " + viewModel.sessionDao.addNew(x)) }
        }
        button3.setOnClickListener {

        }
        button4.setOnClickListener {
            editText.text.toString().let {
                if (it.isNotEmpty()) {
                }
            }
        }



        button5.setOnClickListener {
            thread {
                messageDao.getAll().observe(this@MainActivity, android.arch.lifecycle.Observer {
                    d("CHANGED!!!")
                    it?.let {
                        it.forEach { d("X = $it") }
                        temp = it.firstOrNull()
                    }
                })
            }
        }
        button6.setOnClickListener {
            thread {
                temp?.let {
                    messageDao.update(it.mid, 2)
                }
            }
        }
        button7.setOnClickListener {
            thread {
                    messageDao.addNew(newMESSAGE().copy(status = 3))
            }
        }
        button8.setOnClickListener {
//            messageDao = MyDatabase.getInstance(application, "111").messageDao()
        }

//        messageDao = MyDatabase.getInstance(application, "123").messageDao()
    }

    var temp: MESSAGE? = null
    lateinit var messageDao: MessageDao
}


class ChatViewModel(app: Application) : AndroidViewModel(app) {
//    val messageDao: MessageDao by lazy { MyDatabase.getInstance(app, "123").messageDao() }
//    val sessionDao: SessionDao by lazy { MyDatabase.getInstance(app, "123").sessionDao() }

    lateinit var data: LiveData<List<MESSAGE>>

    fun init(form: String, finish: () -> Unit) {
//        data = messageDao.getAll()
        thread { finish.invoke() }
    }

    fun getSessions() {
//        sessionDao.getAll().forEach {
//            d("Session = $it")
//        }
    }

//    fun addMessage(sid: String) {
//        val message = newMESSAGE().copy(sid = sid, text = "HAHAHA${System.currentTimeMillis()}")
//        thread { messageDao.addNew(message) }
//    }
//
//    fun remove() {
//        data.value?.get(data.value!!.size - 1)?.let {
//            thread { messageDao.remove(it) }
//        }
//    }


}

class MyAdapter(private val layoutInflater: LayoutInflater, private var list: ArrayList<MESSAGE> = ArrayList()) : RecyclerView.Adapter<MyViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.content.text = list[position].text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(layoutInflater.inflate(R.layout.item, parent, false))

    fun setData(newData: List<MESSAGE>) {
        val tmp = ArrayList<MESSAGE>(newData.size)
        newData.forEach { tmp.add(it.clone()) }
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = list.size
            override fun getNewListSize(): Int = newData.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = newData[newItemPosition] == list[oldItemPosition]
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = list[oldItemPosition].mid.contentEquals(newData[newItemPosition].mid)
        }).apply {
            dispatchUpdatesTo(this@MyAdapter)
        }
        list.clear()
        list.addAll(tmp)
    }

    fun addData(msg: BibiProtoApplication.TextMessage) {
        newMESSAGE().copy(text = msg.text).let {
            list.add(it)
        }
        notifyItemInserted(list.size - 1)
    }
}

class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image = view.image!!
    val content = view.content!!
}

fun newMESSAGE() = MESSAGE(UUID.randomUUID().toString(), "", "", "", "", "", "", 1, 1, System.currentTimeMillis(), 1, "", "", "", "", "", "",0)

fun newSESSION() = SESSION("", "", "", "", 1, 1, 1, 1, 1, System.currentTimeMillis(), 1, "")
