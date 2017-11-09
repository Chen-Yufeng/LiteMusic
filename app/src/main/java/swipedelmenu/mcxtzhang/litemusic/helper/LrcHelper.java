package swipedelmenu.mcxtzhang.litemusic.helper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import swipedelmenu.mcxtzhang.litemusic.entity.LrcLine;

import static swipedelmenu.mcxtzhang.litemusic.MusicListActivity.TAG;

/**
 * Created by daily on 11/8/17.
 */

public class LrcHelper {
    private BufferedReader mBufferedReader;
    private String title = "", artist = "", album = "";
    private List<LrcLine> lrcList = new ArrayList<>();

    public List<LrcLine> getLrcList(File lrcFile) {
        String line;
        try {
            mBufferedReader = new BufferedReader(new FileReader(lrcFile));

            while ((line = mBufferedReader.readLine()) != null) {
//                Log.d(TAG, "getLrcList: "+lrcList.size());
                if (line.substring(0, 4).equals("[ti:")) {
                    title = line.substring(4, line.lastIndexOf(']'));
                } else if (line.substring(0, 4).equals("[ar:")) {
                    artist = line.substring(4, line.lastIndexOf(']'));
                } else if (line.substring(0, 4).equals("[al:")) {
                    album = line.substring(4, line.lastIndexOf(']'));
                } else {
                    String startTime = line.substring(line.indexOf('[') + 1, line.lastIndexOf(']'));
                    int min = Integer.parseInt(startTime.substring(0, startTime.indexOf(':')));
                    int sec = Integer.parseInt(startTime.substring(startTime.indexOf(':') + 1,
                            startTime.indexOf('.')));
                    int mil = Integer.parseInt(startTime.substring(startTime.indexOf('.') + 1));
                    long milliSecond = min*60*1000+sec*1000+mil;
                    String lrcText = line.substring(line.lastIndexOf(']') + 1);
                    LrcLine lrcLine = new LrcLine(milliSecond,lrcText);
                    lrcList.add(lrcLine);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lrcList;
    }
}
