package cz.test.shazam.data;

public class AudioMatchResult {

	public final String songName;
	private int offset = 0;
	private int targetHashs = 0;
	private int sampleHashs = 0;
	private int matchTimes = 0;

	public AudioMatchResult(String songName) {
		super();
		this.songName = songName;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMatchTimes() {
		return matchTimes;
	}

	public void setMatchTimes(int times) {
		this.matchTimes = times;
	}

	public int getTargetHashs() {
		return targetHashs;
	}

	public void setTargetHashs(int targetHashs) {
		this.targetHashs = targetHashs;
	}

	public int getSampleHashs() {
		return sampleHashs;
	}

	public void setSampleHashs(int sampleHashs) {
		this.sampleHashs = sampleHashs;
	}

	public String dbgStr() {
		return String.format("songName=%s,matchTimes=%d,offset=%d,sampleHashs=%d,confidenceRatio=%f", songName,
				matchTimes, offset, sampleHashs, sampleHashs == 0 ? 0D : (matchTimes * 1D / sampleHashs));
	}

}
