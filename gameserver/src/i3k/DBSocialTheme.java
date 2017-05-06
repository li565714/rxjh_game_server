package i3k;

import java.util.List;
import java.util.Map;

import ket.util.Stream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;

public class DBSocialTheme implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	@Override
	public void decode(Stream.AIStream is) throws EOFException, DecodeException
	{
		int dbVersion = is.popInteger();
		
		latest = is.popList(SBean.DBSocialComment.class);
		likeRank = is.popList(SBean.DBSocialComment.class);
		dislikeRank = is.popList(SBean.DBSocialComment.class);
		
		padding1 = is.popInteger();
		padding2 = is.popInteger();
		padding3 = is.popInteger();
		padding4 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushList(latest);
		os.pushList(likeRank);
		os.pushList(dislikeRank);
		
		os.pushInteger(padding1);
		os.pushInteger(padding2);
		os.pushInteger(padding3);
		os.pushInteger(padding4);
	}

	public List<SBean.DBSocialComment> latest;
	public List<SBean.DBSocialComment> likeRank;
	public List<SBean.DBSocialComment> dislikeRank;
	
	public int padding1;
	public int padding2;
	public int padding3;
	public int padding4;
}
