package com.drumge.template;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class VersionUtil {
	static int sLocalVer[] = null;
	static String sLocalName = null;
    private static final String SNAPSHOT = "-SNAPSHOT";
	
	private static final String DOT = ".";
	public static Ver getVerFromStr(String version) {
        String normalVer = version;
        if (version != null && version.contains(SNAPSHOT)) {
            normalVer = version.replace(SNAPSHOT, "");
        }
	    if (normalVer != null && normalVer.matches("\\d{1,}.\\d{1,}.\\d{1,}")) {
	        Ver ver = new Ver();
	        int dotPos = normalVer.indexOf(DOT);
	        int prevPos = 0;
	        ver.mMajor = Integer.valueOf(normalVer.substring(prevPos, dotPos));
	        prevPos = dotPos + 1;
	        dotPos = normalVer.indexOf(DOT, prevPos);
	        ver.mMinor = Integer.valueOf(normalVer.substring(prevPos, dotPos));
	        prevPos = dotPos + 1;
	        ver.mBuild = Integer.valueOf(normalVer.substring(prevPos));
            ver.isSnapshot = version.contains(SNAPSHOT);
	        return ver;
	    }
	    return null;
	}
	
	public static Ver getLocalVer(Context c) {
        Ver v = new Ver();
        int ver[] = getLocal(c);
        if(ver != null && ver.length > 0) {
            v.mMajor = ver[0];
            if(ver.length > 1) {
                v.mMinor = ver[1];
                if(ver.length > 2) {
                    v.mBuild = ver[2];
                    if(ver.length > 3)
                        v.isSnapshot = ver[3] == 1 ? true : false;
                }
            }
        }
        return v;
    }

	public static String getLocalName(Context c){
		if( sLocalName != null ){
			return sLocalName;
		}

        try {
            loadLoaclVer(c);
        } catch (Exception e) {
            sLocalVer  = new int[4];
            sLocalVer[0] = 0;
            sLocalVer[1] = 0;
            sLocalVer[2] = 0;
            sLocalVer[3] = 0;
        }
		
		return sLocalName;
	}
	
	public static int[] getLocal(Context c){
		if( sLocalVer != null ){
			return sLocalVer.clone();
		}
		try {
		    loadLoaclVer(c);
        } catch (Exception e) {
            sLocalVer  = new int[4];
            sLocalVer[0] = 0;
            sLocalVer[1] = 0;
            sLocalVer[2] = 0;
            sLocalVer[3] = 0;
        }

		return sLocalVer.clone();
	}

    public static int getVersionCode(Context c) {
        int verCode = 0;
        try {
            verCode = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {

        }

        return verCode;
    }

	static void loadLoaclVer(Context c){
		try {
			sLocalName = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Local Ver Package Error");
		}
		
		if( sLocalName == null ){
			throw new RuntimeException("Local Ver VersionName Not Exist");
		}

        Ver ver = VersionUtil.getVerFromStr(sLocalName);
		sLocalVer = ver.toVerCode();
	}

    public static int getSvnBuildVersion(Context context) {
        int svnBuildVer = 0;
        try {
            if (context != null) {
                String pkgName = context.getPackageName();
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
                svnBuildVer = appInfo.metaData.getInt("SvnBuildVersion");
            }
        } catch (Exception e) {
            Log.e("", e.toString());
        }

        return svnBuildVer;
    }

    public static class Ver {
        public int mMajor;
        public int mMinor;
        public int mBuild;
        public boolean isSnapshot;

        public boolean bigThan(Ver v) {
            return (mMajor > v.mMajor) || ( (mMajor == v.mMajor) && (mMinor > v.mMinor) )
                    || ( (mMajor == v.mMajor) && (mMinor == v.mMinor) && (mBuild > v.mBuild) );
        }

        public boolean smallThan(Ver v) {
            return (mMajor < v.mMajor) || ( (mMajor == v.mMajor) && (mMinor < v.mMinor) )
                    || ( (mMajor == v.mMajor) && (mMinor == v.mMinor) && (mBuild < v.mBuild) );
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Ver ver = (Ver) o;

            if (mMajor != ver.mMajor) return false;
            if (mMinor != ver.mMinor) return false;
            return mBuild == ver.mBuild;
        }

        @Override
        public int hashCode() {
            int result = mMajor;
            result = 31 * result + mMinor;
            result = 31 * result + mBuild;
            return result;
        }

        /**
         * 升级时按此版本号请求升级号 by zhongyongsheng
         * @return
         */
        public String getVersionName(Context c) {
            if (isSnapshot) {
                return String.format("%d.%d.%d", 0, getSvnBuildVersion(c), VersionUtil.getVersionCode(c));
            }
            return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
        }

        /**
         * 关于中显示的版本号 by zhongyongsheng
         * @return
         */
        public String aboutDisplayName(Context c) {
            if (isSnapshot) {
                return String.format("%d.%d.%d-%s(%d.%d.%d)%s", mMajor, mMinor, mBuild,
                        "内测版",
                        0, getSvnBuildVersion(c), VersionUtil.getVersionCode(c),
                        BasicConfig.getInstance().isDebuggable() ? "D" : "");
            }
            return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
        }

        /**
         * 反馈上报的版本号 by zhongyongsheng
         * @return
         */
        public String feedbackVersionName(Context c) {
            if (isSnapshot) {
                return String.format("%d.%d.%d-dev(%d.%d.%d)%s", mMajor, mMinor, mBuild,
                        0, getSvnBuildVersion(c), VersionUtil.getVersionCode(c),
                        BasicConfig.getInstance().isDebuggable()  ? "D" : "");
            }
            return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
        }

        public String toString() {
            if (isSnapshot) {
                return String.format("%d.%d.%d(SNAPSHOT, Build %s)", mMajor, mMinor, mBuild, VersionUtil.getVersionCode(BasicConfig.getInstance().getAppContext()));
            }
            return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
        }

        public int[] toVerCode() {
            int[] ver = new int[4];
            ver[0] = mMajor;
            ver[1] = mMinor;
            ver[2] = mBuild;
            ver[3] = isSnapshot ? 1 : 0;

            return ver;
        }

        public String getVersionNameWithoutSnapshot(){
            return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
        }

        /*
        * version name for request of *.3g.yy.com
        * @author zongbao
        * @since 3.0
        * */
        public String getVersionNameFor3GReq() {
            String versionName = getVersionNameWithoutSnapshot();
            if (isSnapshot || BasicConfig.getInstance().isDebuggable()) {
                versionName += "_beta";
            }
            return versionName;
        }

        /**
         * 获取manifiest的原始versionname
         * @return
         */
        public String getOriginalVersion() {
            return sLocalName;
        }

    };

}
