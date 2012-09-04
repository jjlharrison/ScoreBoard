package dk.frankbille.scoreboard.components;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.player.PlayerPage;
import dk.frankbille.scoreboard.ratings.GamePlayerRating;
import dk.frankbille.scoreboard.ratings.GameRating;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;

public class GameTeamPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat RATING_VALUE = new DecimalFormat("#,##0");
	private static final DecimalFormat RATING_CHANGE = new DecimalFormat("+0.0;-0.0");

	public GameTeamPanel(String id, final IModel<GameTeam> model, final IModel<Player> selectedPlayerModel) {
		super(id, model);
		
		final IModel<List<Player>> playersModel = new LoadableDetachableModel<List<Player>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Player> load() {
				GameTeam gameTeam = model.getObject();
				Team team = gameTeam.getTeam();
				List<Player> players = new ArrayList<Player>(team.getPlayers());
				Collections.sort(players, new PlayerComparator());
				return players;
			}
		};
		
		// Popover
		add(AttributeModifier.replace("rel", "popover-top"));
		add(AttributeModifier.replace("title", new StringResourceModel("rating", null)));
		add(AttributeModifier.replace("data-content", new AbstractReadOnlyModel<CharSequence>() {
			private static final long serialVersionUID = 1L;

			@Override
			public CharSequence getObject() {
				StringBuilder b = new StringBuilder();
				
				b.append("<small>");
				
				GameTeam gameTeam = model.getObject();
				List<Player> players = playersModel.getObject();
				
				RatingCalculator rating = RatingProvider.getRatings();
				Localizer localizer = Application.get().getResourceSettings().getLocalizer();
				
				// Player ratings
				for (Player player : players) {
					b.append(player.getName()).append(" (");

					GamePlayerRating playerRating = rating.getGamePlayerRating(gameTeam.getGame().getId(), player.getId());
					b.append(RATING_VALUE.format(playerRating.getRating()));
					
					b.append(")<br>");
				}
				
				// Team rating
				GameRating gameRatingChange = rating.getGameRatingChange(gameTeam.getGame().getId());
				b.append(localizer.getString("team", GameTeamPanel.this)).append(": ");
				if (gameTeam.isWinner()) {
					b.append(RATING_VALUE.format(gameRatingChange.getWinnerRating()));
				} else {
					b.append(RATING_VALUE.format(gameRatingChange.getLoserRating()));
				}
				b.append(" ");
				double change = gameRatingChange.getChange();
				if (false == gameTeam.isWinner()) {
					change = 0 - change;
				}

				int playerCount = players.size();
				if (playerCount > 0) {
					change /= playerCount;
				}
				b.append(RATING_CHANGE.format(change));
				
				b.append("</small>");
				
				return Strings.escapeMarkup(b);
			}
		}));
		
		// Players
		add(new ListView<Player>("players", playersModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Player> item) {
				final Player player = item.getModelObject();
				
				PageParameters pp = new PageParameters();
				pp.set(0, player.getId());
				BookmarkablePageLink<Void> playerLink = new BookmarkablePageLink<Void>("playerLink", PlayerPage.class, pp);
				item.add(playerLink);
				playerLink.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));
				
				item.add(new Label("plus", "+") {
					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return item.getIndex() < getViewSize()-1;
					}
				});
			}
		});
	}

}