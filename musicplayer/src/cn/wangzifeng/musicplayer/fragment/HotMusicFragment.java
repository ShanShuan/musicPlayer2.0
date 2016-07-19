package cn.wangzifeng.musicplayer.fragment;

import java.util.List;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.wangzifeng.musicplayer.R;
import cn.wangzifeng.musicplayer.adapter.SongAdapter;
import cn.wangzifeng.musicplayer.app.PlaySongApplication;
import cn.wangzifeng.musicplayer.entity.Song;
import cn.wangzifeng.musicplayer.entity.SongInfo;
import cn.wangzifeng.musicplayer.entity.SongUrl;
import cn.wangzifeng.musicplayer.modler.SongMolde;
import cn.wangzifeng.musicplayer.modler.SongMolde.CallBack;
import cn.wangzifeng.musicplayer.service.PlaySongService.SongBinder;

public class HotMusicFragment extends Fragment {
	private SongMolde molde;
	private List<Song> songs;
	private ListView lv;
	private SongAdapter songAdapter;
	protected PlaySongApplication app;
	protected SongBinder songBinder;
	private ServiceConnection conn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.hot_song, null);
		lv = (ListView) v.findViewById(R.id.hot_song_list);

		molde = new SongMolde();
		molde.findHotSong(new CallBack() {

			@Override
			public void onSonglistLoaded(List<Song> songs) {
				if (songs == null) {
					Log.i("s", "songs=null");
				}
				HotMusicFragment.this.songs = songs;

				setadapter(songs);
			}
		}, 0, 20);
		setListenners();

		return v;
	}

	private void setListenners() {
		lv.setOnScrollListener(new OnScrollListener() {
			private boolean isOnBottom;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_FLING:

					break;
				case SCROLL_STATE_IDLE:
					if (isOnBottom) {
						molde.findHotSong(new CallBack() {

							@Override
							public void onSonglistLoaded(List<Song> songs) {
								HotMusicFragment.this.songs.addAll(songs);
								songAdapter.notifyDataSetChanged();
							}
						}, songs.size(), 20);
					}
					break;
				case SCROLL_STATE_TOUCH_SCROLL:

					break;

				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
					isOnBottom = true;

				} else {
					isOnBottom = false;
				}
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				app = (PlaySongApplication) getActivity().getApplication();
				app.setSongs(songs);
				app.motifyIsLocalPlay(false);
				if (app.getPosition() == position) {
					if (app.getFragmentNo() == 2) {//
						if (songBinder.isPlaying()) {
							songBinder.stop();
							return;
						} else {
							songBinder.play();

							return;
						}
					} else {
						final Song s = songs.get(position);
						String song_id = s.getSong_id();

						molde.getSongInfoBySongId(song_id,
								new SongMolde.SongInfoCallback() {
									public void onSongInfoLoaded(
											List<SongUrl> urls, SongInfo info) {
										// �жϻ�ȡ���������Ƿ���null
										if (urls == null || info == null) {
											Toast.makeText(getActivity(),
													"���ּ���ʧ��, �������",
													Toast.LENGTH_SHORT).show();
											return;
										}
										// ��ʼ׼������ ����
										s.setUrls(urls);
										s.setSongInfo(info);
										// ��ȡ��ǰ��Ҫ���ŵ����ֵ�·��
										SongUrl url = urls.get(0);
										String musicpath = url.getShow_link();
										// ��ʼ��������
										songBinder.playMusic(musicpath);
									}
								});
					}
					
				}else{
				app.setPosition(position);
				final Song s = songs.get(position);
				String song_id = s.getSong_id();

				molde.getSongInfoBySongId(song_id,
						new SongMolde.SongInfoCallback() {
							public void onSongInfoLoaded(List<SongUrl> urls,
									SongInfo info) {
								// �жϻ�ȡ���������Ƿ���null
								if (urls == null || info == null) {
									Toast.makeText(getActivity(),
											"���ּ���ʧ��, �������", Toast.LENGTH_SHORT)
											.show();
									return;
								}
								// ��ʼ׼������ ����
								s.setUrls(urls);
								s.setSongInfo(info);
								// ��ȡ��ǰ��Ҫ���ŵ����ֵ�·��
								SongUrl url = urls.get(0);
								String musicpath = url.getShow_link();
								// ��ʼ��������
								songBinder.playMusic(musicpath);
							}
						});
			}
				app.setFragmentNo(2);
			}

		});
	}

	protected void setadapter(List<Song> songs) {
		songAdapter = new SongAdapter(getActivity(), songs, lv);
		lv.setAdapter(songAdapter);

	}

	public void setSongBinder(SongBinder songBinder2) {
		this.songBinder = songBinder2;

	}

}
