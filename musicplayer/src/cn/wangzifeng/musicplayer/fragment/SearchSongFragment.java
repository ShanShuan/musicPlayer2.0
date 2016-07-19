package cn.wangzifeng.musicplayer.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.wangzifeng.musicplayer.R;
import cn.wangzifeng.musicplayer.adapter.SearchSongAdapter;
import cn.wangzifeng.musicplayer.app.PlaySongApplication;
import cn.wangzifeng.musicplayer.entity.Song;
import cn.wangzifeng.musicplayer.entity.SongInfo;
import cn.wangzifeng.musicplayer.entity.SongUrl;
import cn.wangzifeng.musicplayer.modler.SongMolde;
import cn.wangzifeng.musicplayer.modler.SongMolde.CallBack;
import cn.wangzifeng.musicplayer.service.PlaySongService.SongBinder;

public class SearchSongFragment extends Fragment{
	private ImageView iv;
	private EditText etSearchSong;
	private ListView lvSearchSong;
	private SongMolde molde;
	private List<Song> songs;
	private PlaySongApplication app;
	private SongBinder songBinder;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=View.inflate(getActivity(), R.layout.search_song, null);
		setViews(view);
		app=(PlaySongApplication)getActivity().getApplication();
		setListenners();
		molde=new SongMolde();
		return view;
	}
	private void setListenners() {
		//�������һ����ť
		lvSearchSong.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				app.setSongs(songs);
				app.setPosition(position);
				app.motifyIsLocalPlay(false);
				final Song s=songs.get(position);
				String song_id=s.getSong_id();
				
				molde.getSongInfoBySongId(song_id, new SongMolde.SongInfoCallback() {
					public void onSongInfoLoaded(List<SongUrl> urls,SongInfo info) {
						//�жϻ�ȡ���������Ƿ���null 
						if(urls == null || info==null){
							Toast.makeText(getActivity(), "���ּ���ʧ��, �������", Toast.LENGTH_SHORT).show();
							return;
						}
						//��ʼ׼������                                    ����
						s.setUrls(urls);
						s.setSongInfo(info);
						//��ȡ��ǰ��Ҫ���ŵ����ֵ�·��
						SongUrl url = urls.get(0);
						String musicpath=url.getShow_link();
						Log.i("show lick", musicpath);
						if(musicpath==null||"".equals(musicpath)){
							Toast.makeText(getActivity(), "����"+s.getTitle()+"��Ҫ��Ա���������Ӱ�!", Toast.LENGTH_SHORT).show();
							return;
						}
						//��ʼ��������
						songBinder.playMusic(musicpath);
					}
				});
			}
		});
		
		
		
		//���������ť ��ѯ ����
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path=etSearchSong.getText().toString();
				if("".equals(path)){
					Toast.makeText(getActivity(), "������ؼ��֣���", Toast.LENGTH_SHORT).show();
					return;
				}
				molde.searchSong(path, 1, new CallBack() {
					
					@Override
					public void onSonglistLoaded(List<Song> songs) {
						SearchSongFragment.this.songs=songs;
						SearchSongAdapter adapter=new SearchSongAdapter(getActivity(), songs, lvSearchSong);
						lvSearchSong.setAdapter(adapter);						
					}
				});
			}
		});
		
		
		
		etSearchSong.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>0){
				iv.setVisibility(View.VISIBLE);
				}else{
					iv.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	private void setViews(View v) {
		iv=(ImageView) v.findViewById(R.id.iv_search_song);
		etSearchSong=(EditText) v.findViewById(R.id.et_search_song);
		lvSearchSong=(ListView) v.findViewById(R.id.lv_search_song);
	}
	public void setSongBinder(SongBinder songBinder) {
		this.songBinder=songBinder;
	}
}
