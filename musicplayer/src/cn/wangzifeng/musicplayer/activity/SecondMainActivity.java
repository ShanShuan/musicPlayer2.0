package cn.wangzifeng.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import cn.wangzifeng.musicplayer.R;
import cn.wangzifeng.musicplayer.app.PlaySongApplication;
import cn.wangzifeng.musicplayer.entity.GlobalConsts;
import cn.wangzifeng.musicplayer.entity.LrcLine;
import cn.wangzifeng.musicplayer.entity.Music;
import cn.wangzifeng.musicplayer.entity.Song;
import cn.wangzifeng.musicplayer.entity.SongInfo;
import cn.wangzifeng.musicplayer.entity.SongUrl;
import cn.wangzifeng.musicplayer.fragment.HotMusicFragment;
import cn.wangzifeng.musicplayer.fragment.LocalMusicFragment;
import cn.wangzifeng.musicplayer.fragment.NewSongFragment;
import cn.wangzifeng.musicplayer.fragment.SearchSongFragment;
import cn.wangzifeng.musicplayer.modler.SongMolde;
import cn.wangzifeng.musicplayer.modler.SongMolde.LrcCallBack;
import cn.wangzifeng.musicplayer.modler.SongMolde.SongInfoCallback;
import cn.wangzifeng.musicplayer.service.PlaySongService;
import cn.wangzifeng.musicplayer.service.PlaySongService.SongBinder;
import cn.wangzifeng.musicplayer.util.BitmapUtils;
import cn.wangzifeng.musicplayer.util.BitmapUtils.BitmapCallback;

public class SecondMainActivity extends FragmentActivity {
	private ViewPager vp;
	private FragmentPagerAdapter fragmentPagerAdapter;
	private ArrayList<Fragment> fragments;
	private RadioGroup rg;
	private RadioButton rbLocal;
	private RadioButton rbNew;
	private RadioButton rbHot;
	private RadioButton rbSearch;
	private RelativeLayout rgController;
	private ImageView ivDisplay;
	private MyBraodcast receiver;
	public PlaySongApplication app;
	public List<Song> songs;
	private ImageView ivNext;
	private ImageView ivPlayorPause;
	private ImageView ivpervious;
	protected SongBinder songBinder;
	private ServiceConnection conn;
	protected int p;
	private SongMolde molde;
	protected RotateAnimation animation = null;
	protected Song s;
	private int loopFlage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_main);
		app=PlaySongApplication.getContext();
		loopFlage=app.getLoopFlag();
		try {
			app.removeSound();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setViews();
		setVp();
		molde = new SongMolde();
		setListenners();
		registerBraodcast();
		bindService();

	}
	@Override
	protected void onResume() {
		loopFlage=app.getLoopFlag();
		super.onResume();
	}

	private void bindService() {
		Intent service = new Intent(this, PlaySongService.class);
		conn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				songBinder = (SongBinder) service;
				NewSongFragment f = (NewSongFragment) fragments.get(1);
				HotMusicFragment h = (HotMusicFragment) fragments.get(2);
				SearchSongFragment s = (SearchSongFragment) fragments.get(3);
				LocalMusicFragment l = (LocalMusicFragment) fragments.get(0);
				s.setSongBinder(songBinder);
				f.setSongBinder(songBinder);
				h.setSongBinder(songBinder);
				l.setSongBinder(songBinder);
			}
		};
		int flags = Service.BIND_AUTO_CREATE;
		bindService(service, conn, flags);

	}

	/**
	 * ע��㲥
	 */
	private void registerBraodcast() {
		receiver = new MyBraodcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_START_PLAY);
		filter.addAction(GlobalConsts.ACTION_STOP_PLAY);
		filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder b = new Builder(SecondMainActivity.this);
		b.setTitle("��ʾ��Ϣ").setMessage("��ѡ���˳�?���Ǻ�̨����?")
				.setPositiveButton("��̨", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent setIntent = new Intent(Intent.ACTION_MAIN);
				        setIntent.addCategory(Intent.CATEGORY_HOME);
				        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        startActivity(setIntent);
					}
				})
				.setNegativeButton("�˳�", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();

					}
				}).create();
		b.show();

	}

	/**
	 * �㲥������
	 */
	class MyBraodcast extends BroadcastReceiver {

		private boolean isLocalPlay;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			app = (PlaySongApplication) getApplication();
			isLocalPlay = app.isLocalPlay();
			if (GlobalConsts.ACTION_START_PLAY.equals(action)) {
				if (!isLocalPlay) {
					// ��ʼ��������
					if (rgController.getVisibility() == View.INVISIBLE) {
						rgController.setVisibility(View.VISIBLE);
						TranslateAnimation animation = new TranslateAnimation(
								-rgController.getWidth(), 0, 0, 0);
						animation.setDuration(1000);
						LinearInterpolator i = new LinearInterpolator();
						animation.setInterpolator(i);
						rgController.startAnimation(animation);
					}

					// ���ظ��

					Song s = app.getSongs().get(app.getPosition());
					if(s.getSongInfo()!=null){
					molde.downLoadLrc(SecondMainActivity.this, s.getSongInfo()
							.getLrclink(), new LrcCallBack() {

						@Override
						public void onLrcLoaded(List<LrcLine> lrcs) {
							app.setLines(lrcs);
						}
					});
					}
					// ����ivDisplay
					songs = app.getSongs();
					String path = songs.get(app.getPosition()).getPic_small();
					if (path == null || "".equals(path)) {
						ivDisplay.setImageResource(R.drawable.ic_launcher);
						return;
					}
					BitmapUtils.loadBitmap(context, path, new BitmapCallback() {

						@Override
						public void onBitmapLoaded(Bitmap bitmap) {
							ivDisplay.setImageBitmap(bitmap);

						}
					}, 50, 50);
				} else {
					// TODO ���ز���
					if (rgController.getVisibility() == View.INVISIBLE) {
						rgController.setVisibility(View.VISIBLE);
					}
					// ����ivDisplay
					Music m=app.getMusics().get(app.getPosition());
					if(m.getAlbumArt()==null){
						ivDisplay.setImageResource(R.drawable.girl);
					}else{
						Bitmap bm=BitmapFactory.decodeFile(m.getAlbumArt());
						ivDisplay.setImageBitmap(bm);
					}
				}

			} else if (GlobalConsts.ACTION_STOP_PLAY.equals(action)) {
				ivDisplay.clearAnimation();
				animation = null;
			} else if (GlobalConsts.ACTION_UPDATE_PROGRESS.equals(action)) {
					if (animation == null) {
						animation = new RotateAnimation(0, 359,
								ivDisplay.getWidth() / 2,
								ivDisplay.getHeight() / 2);
						animation.setDuration(5000);
						animation.setRepeatCount(-1);
						LinearInterpolator i = new LinearInterpolator();
						animation.setInterpolator(i);
						ivDisplay.startAnimation(animation);
					}
			}
		}

	}

	/**
	 * ��vp����adapter fragments
	 */
	private void setVp() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new LocalMusicFragment());
		fragments.add(new NewSongFragment());
		fragments.add(new HotMusicFragment());
		fragments.add(new SearchSongFragment());
		fragmentPagerAdapter = new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}
		};
		vp.setAdapter(fragmentPagerAdapter);
		vp.setOffscreenPageLimit(4);

	}

	/**
	 * ���ü�����
	 */
	private void setListenners() {
		ivNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!app.isLocalPlay()){//���ǲ��ű�������
					if(loopFlage==1){
					app.next();
				s = app.getSongs().get(app.getPosition());
				molde.getSongInfoBySongId(s.getSong_id(),
						new SongInfoCallback() {

							@Override
							public void onSongInfoLoaded(List<SongUrl> url,
									SongInfo info) {
								s.setUrls(url);
								s.setSongInfo(info);
								String path = s.getUrls().get(0).getShow_link();
								if (path != null || !("".equals(path))) {
									songBinder.playMusic(path);
								} else {
									Toast.makeText(
											SecondMainActivity.this,
											"����" + s.getTitle()
													+ "���Ա����������ѡ����������",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
					}else if(loopFlage==2){
						songBinder.internetOneSongPlay();
					}else if(loopFlage==3){
						songBinder.InternetRandomPlay();
					}
			}else{//���ű�������
				if(loopFlage==1){
				Music m=app.getMusics().get(app.getPosition());
				songBinder.playMusic(m.getPath());
				}else if(loopFlage==2){
					songBinder.LocalOneSongPlay();
				}else if(loopFlage==3){
					songBinder.LocalRandomPlay();
				}
			}
			}
		});
		ivpervious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!app.isLocalPlay()){//���ǲ��ű�������
					if(loopFlage==1){
					app.previous();
				final Song s = app.getSongs().get(app.getPosition());
				molde.getSongInfoBySongId(s.getSong_id(),
						new SongInfoCallback() {

							@Override
							public void onSongInfoLoaded(List<SongUrl> url,
									SongInfo info) {
								s.setUrls(url);
								s.setSongInfo(info);
								String path = s.getUrls().get(0).getShow_link();
								if (path != null || !("".equals(path))) {
									songBinder.playMusic(path);
								} else {
									Toast.makeText(
											SecondMainActivity.this,
											"����" + s.getTitle()
													+ "���Ա����������ѡ����������",
											Toast.LENGTH_SHORT).show();
								}
							}
							
						});
				}else if(loopFlage==2){
					songBinder.internetOneSongPlay();
				}else if(loopFlage==3){
					songBinder.InternetRandomPlay();
				}
			}else{//���ű�������
				if(loopFlage==1){
				Music m=app.getMusics().get(app.getPosition());
				songBinder.playMusic(m.getPath());
				}else if(loopFlage==2){
					songBinder.LocalOneSongPlay();
				}else if(loopFlage==3){
					songBinder.LocalRandomPlay();
				}
			}
			}
		});

		ivPlayorPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				songBinder.playOrPause();

			}
		});

		/**
		 * ivDisplay �����¼�
		 */
		ivDisplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SecondMainActivity.this,
						PlayOneSongActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});
		/**
		 * ʵ�ֵ����ť��Fragment����
		 */
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_local_music:
					vp.setCurrentItem(0);
					break;
				case R.id.rb_new_music:
					vp.setCurrentItem(1);
					break;
				case R.id.rb_hot_music:
					vp.setCurrentItem(2);
					break;
				case R.id.rb_search_music:
					vp.setCurrentItem(3);
					break;

				}
			}
		});
		/**
		 * ��vp����ҳ��仯������
		 */
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					rbLocal.setChecked(true);
					break;
				case 1:
					rbNew.setChecked(true);
					break;
				case 2:
					rbHot.setChecked(true);
					break;
				case 3:
					rbSearch.setChecked(true);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void setViews() {
		ivPlayorPause = (ImageView) findViewById(R.id.iv_pause_or_play);
		ivpervious = (ImageView) findViewById(R.id.iv_previous);
		ivNext = (ImageView) findViewById(R.id.iv_next);
		rgController = (RelativeLayout) findViewById(R.id.rl_controller);
		ivDisplay = (ImageView) findViewById(R.id.iv_display);
		rbNew = (RadioButton) findViewById(R.id.rb_new_music);
		rbHot = (RadioButton) findViewById(R.id.rb_hot_music);
		rbSearch = (RadioButton) findViewById(R.id.rb_search_music);
		rbLocal = (RadioButton) findViewById(R.id.rb_local_music);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		vp = (ViewPager) findViewById(R.id.vp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		unbindService(conn);
		super.onDestroy();
	}

}
