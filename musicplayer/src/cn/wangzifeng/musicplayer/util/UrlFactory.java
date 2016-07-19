package cn.wangzifeng.musicplayer.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlFactory {
	public static String getNewMusicUrl(int offsize,int size){
		return "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0" +
				"&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offsize+"&size="+size; 
	}
	/**
	 * ��ѯ������ϸ��Ϣ��url��ַ
	 * @param songId
	 * @return
	 */
	public static String getSongInfoUrl(String songId) {
		String path = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="+songId+"&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
		return path;
	}
	/**
	 * ���ݹؼ��ֲ�ѯ�����б�Ľӿڵ�ַ
	 * @param key
	 * @param pageno  ҳ��
	 * @param pagesize    ÿҳ������
	 * @return
	 */
	public static String getSearchSongUrl(String key, int pageno, int pagesize){
		try {
			key = URLEncoder.encode(key, "utf-8");
			String url ="http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.search.common&format=json&query="+key+"&page_no="+pageno+"&page_size="+pagesize;
			return url;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getHotMusicUrl(int offset, int size) {
		return "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=2&offset="+offset+"&size="+size; 
	}
}
