package net.pms.external.xbmc.folders.tv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.folders.ListFolder;
import net.pms.external.xbmc.folders.TitleVirtualFolder;

public class TVShowFolder extends VirtualFolder {

	private VideoDAO dao;
	private int tvShowId;

	public TVShowFolder(VideoDAO dao, int tvShowId, String title) {
		super(title, null);
		this.dao = dao;
		this.tvShowId = tvShowId;
	}

	@Override
	public void discoverChildren() {

		XBMCLog.logTimeStart("discovering seasons for " + getName());
		final Map<String, String> seasons = dao.getSeasons(tvShowId);
		XBMCLog.logTimeStop();

		Iterator<String> seasonsIter = seasons.keySet().iterator();
		while (seasonsIter.hasNext()) {
			final String seasonId = seasonsIter.next();
			ListFolder seasonFolder = new ListFolder(seasons.get(seasonId)) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.logTimeStart("discovering files for " + seasons.get(seasonId));
					Map<Integer, String> episodes = dao.getEpisodes(tvShowId, seasonId);
					XBMCLog.logTimeStop();
					List<VirtualFolder> episodesList = new ArrayList<VirtualFolder>();
					for (Integer id : episodes.keySet()) {
						String title = episodes.get(id);
						TitleVirtualFolder titleFolder = new TitleVirtualFolder(id, title, dao);
						episodesList.add(titleFolder);
					}
					return episodesList;
				}
			};
			addChild(seasonFolder);
		}
	}
}
