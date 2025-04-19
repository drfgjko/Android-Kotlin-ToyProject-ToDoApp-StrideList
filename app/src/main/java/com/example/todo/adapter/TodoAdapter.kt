package com.example.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.ui.todos.ToDoListActivity
import com.example.todo.bean.TodoItem


class TodoAdapter(private val todoItems: MutableList<TodoItem>,
                  private val onTodoItemEditListener: ToDoListActivity
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_todolist_rec, parent, false)
        return TodoViewHolder(itemView)
    }

//    fun updateTodoItems(newItems: List<TodoItem>) {
//        todoItems.clear()
//        todoItems.addAll(newItems)
//        notifyDataSetChanged()
//    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoItems[position]
        holder.contentTextView.text = currentItem.content
        holder.deadlineTextView.text = currentItem.deadline
        holder.editButton.setOnClickListener {
//            val intent = Intent(holder.itemView.context, AddToDoActivity::class.java)
//            intent.putExtra("editMode", true)
//            intent.putExtra("todoId", currentItem.id) // 确保existingTodoId是一个有效的ID
//            intent.putExtra("content", currentItem.content)
//            intent.putExtra("deadline", currentItem.deadline)
//            holder.itemView.context.startActivity(intent)
////            holder.itemView.context.startActivityForResult(intent, Companion.REQUEST_CODE_ADD_TODO)
//            holder.contentTextView.text = currentItem.content
//            holder.deadlineTextView.text = currentItem.deadline
//            val currentItem = todoItems[position]
            onTodoItemEditListener.onEditItem(currentItem) // 调用回调方法
        }
        holder.completeButton.setOnClickListener {
//            数据库操作
            TodoApplication.getToDosDataSource().deleteTodoItem(currentItem.id)
//            视图更新
            todoItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, todoItems.size)
            Toast.makeText(holder.itemView.context, "已完成！${currentItem.content}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }
}