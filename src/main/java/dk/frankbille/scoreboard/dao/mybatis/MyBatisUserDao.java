package dk.frankbille.scoreboard.dao.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.dao.UserDao;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.utils.EncryptionUtils;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class MyBatisUserDao implements UserDao {

	private UserMapper userMapper;

	@Autowired
	public MyBatisUserDao(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Override
	public User authenticate(String username, String password) {
		String md5Password = EncryptionUtils.md5Encode(password);
		return userMapper.authenticate(username, md5Password);
	}

	@Override
	public void createUser(User user, String password) {
		userMapper.insertUser(user, EncryptionUtils.md5Encode(password));
	}

	@Override
	public void updateUser(User user) {
		userMapper.updateUser(user);
	}

	@Override
	public User getUserForPlayer(Player player) {
		return userMapper.getUserForPlayer(player);
	}

	@Override
	public boolean hasUserWithUsername(String username) {
		return userMapper.getUserWithUsername(username) != null;
	}

}
