package foo.ree.demos.x05th

import android.content.Context
import android.icu.text.IDNA
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Created by allenzhang on 2019/3/15.
 */
class DeviceInfoListView:FrameLayout{
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mTagAdapter: InfoArrayAdapter
    constructor(context: Context?) : super(context){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context)
    }

    private fun init(context: Context?) {
        initView()
        initEvent()
    }

    private fun initEvent() {
    }

    private fun initView() {
        var view = LayoutInflater.from(context).inflate(R.layout.view_device_info,this)
        mRecycleView = view.findViewById(R.id.rl_device_info) as RecyclerView
        mTagAdapter = InfoArrayAdapter()
        mRecycleView.adapter = mTagAdapter
        mRecycleView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
    }



    public fun setMoreTags(tagList:List<String>?) {
        mTagAdapter.mTagList = tagList
        mTagAdapter.notifyDataSetChanged()
    }


    class InfoArrayAdapter : RecyclerView.Adapter<TagViewHolder>() {
        public lateinit var onClickListener: OnItemClickListener
        public var mTagList:List<String>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_device_info,parent,false)
            view.setOnClickListener {
                onClickListener.onItemClick(it)
            }
            val viewHolder = TagViewHolder(view)
            return viewHolder
        }


        override fun getItemCount(): Int {
            return if (mTagList == null) 0 else mTagList!!.size
        }

        fun getItem(position: Int):String?{
            return mTagList?.get(position)
        }


        override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
            if(mTagList == null||mTagList!!.isEmpty()) {
                return
            }
            var tag = mTagList!!.get(position)
            holder.mTvTag.setText(tag)
        }

    }




    class TagViewHolder:RecyclerView.ViewHolder{
        public lateinit var mTvTag :TextView
        private lateinit var mItemView:View
        constructor(itemView: View) : super(itemView){
            mItemView = itemView!!
            mTvTag = mItemView.findViewById(R.id.tv_device_info) as TextView
        }
    }

    interface OnItemClickListener{
        fun onItemClick(view:View)
    }



}