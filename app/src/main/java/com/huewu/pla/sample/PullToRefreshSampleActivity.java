package com.huewu.pla.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dodola.model.DuitangInfo;
import com.dodowaterfall.Helper;
import com.dodowaterfall.widget.ScaleImageView;
import com.example.android.bitmapfun.util.ImageFetcher;

public class PullToRefreshSampleActivity extends FragmentActivity implements IXListViewListener {
    private ImageFetcher mImageFetcher;
    private XListView mAdapterView = null;
    private StaggeredAdapter mAdapter = null;
    private int currentPage = 2;
    private Document document=null;
    ContentTask task = new ContentTask(this, 2);

    private class ContentTask extends AsyncTask<String, Integer, List<DuitangInfo>> {

        private Context mContext;
        private int mType = 1;

        public ContentTask(Context context, int type) {
            super();
            mContext = context;
            mType = type;
        }

        @Override
        protected List<DuitangInfo> doInBackground(String... params) {
            try {
                return parseNewsJSON(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DuitangInfo> result) {
            if (mType == 1) {
                mAdapter.removeAllDatas();
                mAdapter.addItemTop(result);
                mAdapter.notifyDataSetChanged();
                mAdapterView.stopRefresh();

            } else if (mType == 2) {
                mAdapterView.stopLoadMore();
                mAdapter.addItemLast(result);
                mAdapter.notifyDataSetChanged();
            }

        }

        @Override
        protected void onPreExecute() {
        }

        public List<DuitangInfo> parseNewsJSON(String url) throws IOException {
            List<DuitangInfo> duitangs = new ArrayList<DuitangInfo>();
            String json = "";
            if (Helper.checkConnection(mContext)) {
                try {
                    json = Helper.getStringFromUrl(url);

                } catch (IOException e) {
                    Log.e("IOException is : ", e.toString());
                    e.printStackTrace();
                    return duitangs;
                }
            }
            Log.d("MainActiivty", "json:" + json);
                if(null != json){
                    document= Jsoup.parse(json);
                    Elements es=null;
                    Elements elements=document.getElementsByClass("e m");
                    for (Element element : elements) {
                        DuitangInfo newsInfo = new DuitangInfo();
                        String imageUrl=element.getElementsByTag("img").attr("data-original");
                        if(imageUrl.toString().trim()==null || imageUrl.toString().trim().equals("") ){
                            imageUrl=element.getElementsByTag("img").attr("src");
                            newsInfo.setIsrc(imageUrl);
                        }else {
                            newsInfo.setIsrc(imageUrl);
                        }
                        newsInfo.setHeight(200+(new Random().nextInt(200)));
                        duitangs.add(newsInfo);
                    }
                }

            return duitangs;
        }
    }

    /**
     * 添加内容
     * 
     * @param pageindex
     * @param type
     *            1为下拉刷新 2为加载更多
     */
    private void AddItemToContainer(int pageindex, int type) {
        if (task.getStatus() != Status.RUNNING) {
          //  String url = "http://www.duitang.com/album/1733789/masn/p/" + pageindex + "/24/";
            String url="http://www.topit.me/tag/美女?p="+pageindex;
            Log.d("MainActivity", "current url:" + url);
            ContentTask task = new ContentTask(this, type);
            task.execute(url);

        }
    }

    public class StaggeredAdapter extends BaseAdapter {
        private Context mContext;
        private LinkedList<DuitangInfo> mInfos;
        private XListView mListView;

        public StaggeredAdapter(Context context, XListView xListView) {
            mContext = context;
            mInfos = new LinkedList<DuitangInfo>();
            mListView = xListView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            DuitangInfo duitangInfo = mInfos.get(position);

            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
                convertView = layoutInflator.inflate(R.layout.infos_list, null);
                holder = new ViewHolder();
                holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
               // holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
                convertView.setTag(holder);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(PullToRefreshSampleActivity.this,ConnectActivity.class);
                    startActivity(intent);

                }
            });
            holder = (ViewHolder) convertView.getTag();
            holder.imageView.setImageWidth(duitangInfo.getWidth());
            holder.imageView.setImageHeight(duitangInfo.getHeight());
         //   holder.contentView.setText(duitangInfo.getMsg());
            mImageFetcher.loadImage(duitangInfo.getIsrc(), holder.imageView);
            Log.i("Tag8","duitangInfo:"+duitangInfo.getIsrc());
            return convertView;
        }

        class ViewHolder {
            ScaleImageView imageView;
           // TextView contentView;
           // TextView timeView;
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mInfos.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        public void addItemLast(List<DuitangInfo> datas) {
            mInfos.addAll(datas);
        }

        public void addItemTop(List<DuitangInfo> datas) {
            mInfos.addAll(datas);
        }

        public void removeAllDatas(){
            mInfos.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pull_to_refresh_sample);
        mAdapterView = (XListView) findViewById(R.id.list);
        mAdapterView.setPullLoadEnable(true);
        mAdapterView.setXListViewListener(this);

        mAdapter = new StaggeredAdapter(this, mAdapterView);

        mImageFetcher = new ImageFetcher(this, 240);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        AddItemToContainer(currentPage, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapterView.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        AddItemToContainer(2, 1);

    }

    @Override
    public void onLoadMore() {
        AddItemToContainer(++currentPage, 2);

    }
}// end of class
