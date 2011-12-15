/*
 * Copyright (c) 2007-2011 by The Broad Institute of MIT and Harvard.  All Rights Reserved.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 *
 * THE SOFTWARE IS PROVIDED "AS IS." THE BROAD AND MIT MAKE NO REPRESENTATIONS OR
 * WARRANTES OF ANY KIND CONCERNING THE SOFTWARE, EXPRESS OR IMPLIED, INCLUDING,
 * WITHOUT LIMITATION, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, NONINFRINGEMENT, OR THE ABSENCE OF LATENT OR OTHER DEFECTS, WHETHER
 * OR NOT DISCOVERABLE.  IN NO EVENT SHALL THE BROAD OR MIT, OR THEIR RESPECTIVE
 * TRUSTEES, DIRECTORS, OFFICERS, EMPLOYEES, AND AFFILIATES BE LIABLE FOR ANY DAMAGES
 * OF ANY KIND, INCLUDING, WITHOUT LIMITATION, INCIDENTAL OR CONSEQUENTIAL DAMAGES,
 * ECONOMIC DAMAGES OR INJURY TO PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER
 * THE BROAD OR MIT SHALL BE ADVISED, SHALL HAVE OTHER REASON TO KNOW, OR IN FACT
 * SHALL KNOW OF THE POSSIBILITY OF THE FOREGOING.
 */

package org.broad.igv.data;

import org.broad.igv.feature.LocusScore;
import org.broad.igv.track.TrackType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: jrobinso
 * Date: Dec 19, 2009
 * Time: 7:17:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractDataSourceTest {


    @Before
    public void setUp() {
// Add your code here
    }

    @After
    public void tearDown() {
// Add your code here
    }

    /**
     * # When viewed as a heatmap this feature shows a 1-pixel break at position 23,314,405  when the
     * # zoom is at  chr20:23,314,141-23,314,667
     * chr20   23313029    23316433    91.1
     */
    @Test
    public void testRT_134467() {
        int[] starts = {23313029};
        int[] ends = {23316433};
        float[] values = {91.1f};

        int s = 23313929;
        int e = 23314405;

        TestDataSource ds = new TestDataSource(starts, ends, values);

        SummaryTile tile = ds.computeSummaryTile("chr20", 0, s, e, 1);
        List<LocusScore> scores = tile.getScores();

        // Scores should be within 10 +/- 0.5,  and the mean should be very close to 10

        for (LocusScore score : scores) {
            float v = score.getScore();
            assertEquals(91.1f, v, 0.00001);
        }

        assertEquals(1, scores.size());


    }

    @Test
    public void testGetSummaryScoresForRange() {

        TestDataSource ds = new TestDataSource();

        SummaryTile tile = ds.computeSummaryTile("", 0, 0, 10000, 1);

        List<LocusScore> scores = tile.getScores();

        // Scores should be within 10 +/- 0.5,  and the mean should be very close to 10
        float sum = 0.0f;
        long totPoints = 0;
        for (LocusScore score : scores) {
            float v = score.getScore();
            assertTrue((v >= 9.5f && v <= 10.5f));
            int numPoints = score.getEnd() - score.getStart();
            sum += numPoints * v;
            totPoints += numPoints;
        }
        double mean = sum / totPoints;
        assertEquals(10.0, mean, 1.0e-2);
    }


    class TestDataSource extends AbstractDataSource {


        private int nPts;
        int[] starts;
        int[] ends;
        float[] values;
        String[] probes;

        TestDataSource(int[] starts, int[] ends, float[] values) {
            super(null);
            nPts = starts.length;
            this.starts = starts;
            this.ends = ends;
            this.values = values;
            this.probes = null;
        }

        TestDataSource() {
            super(null);
            nPts = 10000;
            starts = new int[nPts];
            ends = new int[nPts];
            values = new float[nPts];
            probes = new String[nPts];
            for (int i = 0; i < nPts; i++) {
                starts[i] = i;
                ends[i] = i + 1;
                values[i] = (float) (9.5 + Math.random());
                probes[i] = "probe_" + i;
            }


        }

        protected DataTile getRawData(String chr, int startLocation, int endLocation) {
            return new DataTile(starts, ends, values, probes);
        }

        @Override
        protected List<LocusScore> getPrecomputedSummaryScores(String chr, int startLocation, int endLocation, int zoom) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }


        @Override
        public int getLongestFeature(String chr) {
            return 1000;
        }

        public double getMedian(int zoom, String chr) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public double getDataMax() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public double getDataMin() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public TrackType getTrackType() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
