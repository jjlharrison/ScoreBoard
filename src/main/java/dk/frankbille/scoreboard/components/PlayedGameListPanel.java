package dk.frankbille.scoreboard.components;

import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.game.EditGamePage;
import dk.frankbille.scoreboard.security.SecureExecutionBookmarkablePageLink;

public class PlayedGameListPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	public PlayedGameListPanel(String id, IModel<List<Game>> gamesModel, final IModel<Player> selectedPlayerModel) {
		super(id);
		
		setOutputMarkupId(true);
		
		final PaginationModel<Game> paginationModel = new PaginationModel<Game>(gamesModel, 0, 20);

		add(new ListView<Game>("games", paginationModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Game> item) {
				item.add(RowColorModifier.create(item));
				PageParameters pp = new PageParameters();
				pp.set(0, item.getModelObject().getId());
				BookmarkablePageLink<Void> link = new SecureExecutionBookmarkablePageLink<Void>("gameLink", EditGamePage.class, pp);
				item.add(link);
				
				link.add(new DateLabel("date", new PropertyModel<Date>(item.getModel(), "date"), new PatternDateConverter("yyyy-MM-dd", false)));

				//Add the winning and losing team
				Game game = item.getModelObject();
				List<GameTeam> teamsSortedByScore = game.getTeamsSortedByScore();
				item.add(new GameTeamPanel("team1", new Model<GameTeam>(teamsSortedByScore.get(0)), selectedPlayerModel));
				item.add(new GameTeamPanel("team2", new Model<GameTeam>(teamsSortedByScore.get(1)), selectedPlayerModel));

				//Add the game score
				item.add(new Label("score", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						StringBuilder b = new StringBuilder();
						Game game = item.getModelObject();
						List<GameTeam> teamsSortedByScore = game.getTeamsSortedByScore();
						b.append(teamsSortedByScore.get(0).getScore());
						b.append(" : ");
						b.append(teamsSortedByScore.get(1).getScore());
						return b.toString();
					}
				}));
			}
		});
		
		WebMarkupContainer footer = new WebMarkupContainer("footer") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isVisible() {
				return paginationModel.isPaginationNeeded();
			}
		};
		add(footer);
		
		footer.add(new NavigationPanel<Game>("navigation", paginationModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onPageChanged(AjaxRequestTarget target, int selectedPage) {
				target.add(PlayedGameListPanel.this);
			}
		});
	}

}
