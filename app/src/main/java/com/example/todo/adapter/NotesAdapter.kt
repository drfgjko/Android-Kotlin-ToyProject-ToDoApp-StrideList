package com.example.todo.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.utils.TimeUtils
import com.example.todo.bean.Note

class NotesAdapter: RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    //笔记列表数据
    private lateinit var notelist: List<Note>
    //点击事件
    private lateinit var  onItemClickListener: OnItemClickListener

    //视图固定器 -> 获取控件
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val rowLinearLayout:LinearLayout = view.findViewById(R.id.row_linearLayout)
        val lbsTextView :TextView = view.findViewById(R.id.lbs_textView)
        val titleTextView:TextView = view.findViewById(R.id.title_textView)
        val wordCountTextView:TextView = view.findViewById(R.id.wordCount_textView)
        val updateTimeTextView:TextView = view.findViewById(R.id.updateTime_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false)
            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val note = notelist[position]

        holder.rowLinearLayout.setOnClickListener {
            onItemClickListener.onItemClick(note, position)
        }

        holder.lbsTextView.text = note.lbs
        holder.titleTextView.text = note.title
        holder.wordCountTextView.text = "字数 ${note.content.length}个"
        holder.updateTimeTextView.text ="编辑于  ${TimeUtils.getSimpleTime(note.updateTime.time)}"

        //单例类调用方法，处理时间数据
        TimeUtils.getSimpleTime(note.updateTime.time)

    }

    fun setNewData(list: List<Note>) {
        this.notelist = list
//        对眼睛不好所以报警告
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note, position: Int)
    }

    override fun getItemCount(): Int {
        return notelist.size
    }

}
