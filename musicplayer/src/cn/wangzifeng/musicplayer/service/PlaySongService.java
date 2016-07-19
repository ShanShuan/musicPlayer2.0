package cn.wangzifeng.musicplayer.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import cn.wangzifeng.musicplayer.app.PlaySongApplication;
import cn.wangzifeng.musicplayer.entity.GlobalConsts;
import cn.wangzifeng.musicplayer.entity.Music;
import cn.wangzifeng.musicplayer.entity.Song;
import cn.wangzifeng.musicplayer.entity.SongInfo;
import cn.wangzifeng.musicplayer.entity.SongUrl;
import cn.wangzifeng.musicplayer.modler.SongMolde;

public class PlaySongService extends Service {

	private MediaPlayer player;
	private boolean isLoop = true;
	private boolean isPlayed = false;
	protected PlaySongApplication app;
	private String musicpath;
	private SongMolde molde;
	private int loopFlag;
	Random random1;
	Random random2;
	private SongBinder binder;
	@Override
	public void onCreate() {
		app = PlaySongApplication.getContext();
		random1=new Random();
		random2=new Random();
		molde = new SongMolde();
		// ��ʼ��������
		player = new MediaPlayer();
		player.setOnPreparedListener(new OnPreparedListener() {
			// prepare��ɺ� ִ�и÷���
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				isPlayed = true;
				// �����Զ���㲥 ����Activity �����Ѿ���ʼ����
				Intent i = new Intent(GlobalConsts.ACTION_START_PLAY);
				sendBroadcast(i);
			}
		});
		// ���������߳� ÿ1���Activity��һ�ι㲥
		new WorkThread().start();
		complete();
	}

	private void complete() {
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				loopFlag=app.getLoopFlag();
				if(loopFlag==1){
					
				app.next();
				if(!app.isLocalPlay()){
				final Song s = app.getSongs().get(app.getPosition());
				String song_id = s.getSong_id();
				
				molde.getSongInfoBySongId(song_id,
						new SongMolde.SongInfoCallback() {

							public void onSongInfoLoaded(List<SongUrl> urls,
									SongInfo info) {
								// �жϻ�ȡ���������Ƿ���null
								if (urls == null || info == null) {
									return;
								}
								// ��ʼ׼������ ����
								s.setUrls(urls);
								s.setSongInfo(info);
								// ��ȡ��ǰ��Ҫ���ŵ����ֵ�·��
								SongUrl url = urls.get(0);
								musicpath = url.getShow_link();
								Log.i("musicPath", musicpath);
								try {
									player.reset();
									player.setDataSource(musicpath);
									player.prepareAsync();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (SecurityException e) {
									e.printStackTrace();
								} catch (IllegalStateException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
				}else{
					try {
						Log.i("123", "�Զ�������һ��");
						Music music=app.getMusics().get(app.getPosition());
						
						String url=music.getPath();
						Log.i("url", ""+url);
						player.reset();
						player.setDataSource(url);
						player.prepareAsync();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}

			}else if(loopFlag==2){
				
				// ����ѭ��
				
				if(!app.isLocalPlay()){
					//���ǲ��ű�������
					 binder.internetOneSongPlay();
					
				}else{
					//���ű�������
					
					binder.LocalOneSongPlay();
					
				}
			}else if(loopFlag==3){
				// �������
				if(!app.isLocalPlay()){
					//���ǲ��ű�������
					
					binder.InternetRandomPlay();
				}else{
					
					binder.LocalRandomPlay();
				}
			}
				
			}

		

			

			

	
		});
	}

	// ÿ1���Activity��һ�ι㲥
	class WorkThread extends Thread {
		@Override
		public void run() {
			while (isLoop) {
				try {
					Thread.sleep(1000);
					// ���͹㲥
					if (player!=null&&player.isPlaying()) {
						Intent i = new Intent(GlobalConsts.ACTION_UPDATE_PROGRESS);
						i.putExtra("current", player.getCurrentPosition());
						i.putExtra("total", player.getDuration());
						sendBroadcast(i);
					} else {
						Intent i = new Intent(GlobalConsts.ACTION_STOP_PLAY);
						sendBroadcast(i);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ���пͻ��˰󶨸�serviceʱ ִ�� context.bindService()
	 */
	public IBinder onBind(Intent intent) {
		binder=new SongBinder();
		return binder;
	}

	/**
	 * ���ظ��ͻ��˵�binder���� �������Ÿ��ͻ��˵��õĽӿڷ���
	 */
	public class SongBinder extends Binder {
		//�����������
		public void LocalRandomPlay() {
			try {
				int p2=random2.nextInt(app.getMusics().size());
				app.setPosition(p2);
				Music music=app.getMusics().get(p2);
				String url=music.getPath();
				player.reset();
				player.setDataSource(url);
				player.prepareAsync();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//�����������
		public void InternetRandomPlay() {
			int p=random1.nextInt(app.getSongs().size());
			app.setPosition(p);
			Log.i("p", "λ��"+p);
			final Song s=app.getSongs().get(p);
			
			molde.getSongInfoBySongId(s.getSong_id(),
					new SongMolde.SongInfoCallback() {

						public void onSongInfoLoaded(List<SongUrl> urls,
								SongInfo info) {
							// �жϻ�ȡ���������Ƿ���null
							if (urls == null || info == null) {
								return;
							}
							// ��ʼ׼������ ����
							s.setUrls(urls);
							s.setSongInfo(info);
							// ��ȡ��ǰ��Ҫ���ŵ����ֵ�·��
							SongUrl url = urls.get(0);
							musicpath = url.getShow_link();
							Log.i("musicPath", musicpath);
							try {
								player.reset();
								player.setDataSource(musicpath);
								player.prepareAsync();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
		}
		//���ص���ѭ������
		public void LocalOneSongPlay() {
			try {
				Music music=app.getMusics().get(app.getPosition());
				
				String url=music.getPath();
				player.reset();
				player.setDataSource(url);
				player.prepareAsync();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//����ת̨�ĵ���ѭ������
		public void internetOneSongPlay() {
			Song s = app.getSongs().get(app.getPosition());
			try {
				player.reset();
				player.setDataSource(s.getUrls().get(0).getShow_link());
				player.prepareAsync();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void stop() {
			player.pause();
		}

		/**
		 * ��ת��ĳ������λ��
		 * 
		 * @param progress
		 */
		public void seekTo(int progress) {
			player.seekTo(progress);
		}

		// ��ͣ�򲥷�
		public void playOrPause() {
			if (player.isPlaying()) {
				player.pause();
			} else {
				player.start();
			}
		}

		/**
		 * ��������
		 * 
		 * @param url
		 *            ���ֵ�·��
		 */
		public void playMusic(String url) {
			try {
				if(!app.isLocalPlay()){
				player.reset();
				player.setDataSource(url);
				// �첽����������Ϣ
				player.prepareAsync();
				// ��player׼����ɺ� ִ��start����
				// ��player���ü���
				}else{
					player.reset();
					player.setDataSource(url);
					player.prepare();
					player.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean isPlaying() {
			if (player.isPlaying()) {
				return true;
			} else {
				return false;
			}
		}

		public void play() {
			player.start();
		}

		public boolean isPlayed() {
			if (isPlayed) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void onDestroy() {
		if (player.isPlaying()) {
			player.stop();
		}
		player = null;
		super.onDestroy();
	}
}
