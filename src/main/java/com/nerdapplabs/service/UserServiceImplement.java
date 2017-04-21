package com.nerdapplabs.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.nerdapplabs.dao.*;
import com.nerdapplabs.model.*;

@Service
public class UserServiceImplement implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AssetDao assetDao;

	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JavaMailSender mailService;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	int count = 0;

	public UserServiceImplement(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	Connection connection;

	@Override
	@Transactional
	public void save(User registerUser) {
		userDao.save(registerUser);
	}

	@Override
	@Transactional
	public void saveAsset(AssetRequest requestasset, User user) {
		String sql = "insert into requestasset (assetname,assettype,quantity,reason,date,email)values('"
				+ requestasset.getAssetname() + "','" + requestasset.getAssettype() + "','" + requestasset.getQuantity()
				+ "','" + requestasset.getReason() + "','" + requestasset.getRequestdate() + "','" + user.getEmail()
				+ "')";
		jdbcTemplate.update(sql);
	}

	@Override
	@Transactional
	public void delete(String email) {
		userDao.delete(email);
	}

	@Override
	@Transactional
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	@Override
	@Transactional
	public User loginUser(String email, String password) {
		User user = this.findByEmail(email);
		if (user != null && user.getPassword().equals(password) && user.getStatus() == 1) {
			return user;
		}
		return null;
	}

	@Override
	public User getUserByEmail(String email) {
		String sql = "select * from user where email=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { email }, new BeanPropertyRowMapper<User>(User.class));
	}

	@Override
	public int update(User user) {
		String sql = "update user set firstname='" + user.getFirstname() + "',lastname='" + user.getLastname()
				+ "', designation='" + user.getDesignation() + "', role='" + user.getRole() + "' where email ='"
				+ user.getEmail() + "'";
		return jdbcTemplate.update(sql);
	}

	public int softDelete(String email) {
		String sql = "update user set status = 0 where email = '" + email + "'";
		return jdbcTemplate.update(sql);
	}

	public User getUser(String email) {
		return userDao.findOne(email);
	}

	@Override
	public List<User> list() {
		String sql = "select email,firstname,designation,role,lastname from user where status = 1";
		List<User> listUsers = jdbcTemplate.query(sql, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User aUser = new User();

				aUser.setEmail(rs.getString("email"));
				aUser.setFirstname(rs.getString("firstname"));
				aUser.setLastname(rs.getString("lastname"));
				aUser.setDesignation(rs.getString("designation"));
				aUser.setRole(rs.getString("role"));

				return aUser;
			}
		});
		return listUsers;
	}

	@Override
	public void sendEmail(String email) {
		User user = this.findByEmail(email);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Recovery Password");
		message.setText("hello " + user.getFirstname() + " your recovery password is " + user.getPassword());
		try {
			mailService.send(message);
			System.out.println("mail sent");
		} catch (MailException e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public User getUserToken(String verificationToken) {
		User user = tokenRepository.findByToken(verificationToken).getUser();
		return user;
	}

	@Override
	public void createVerificationToken(User user, String token) {
		VerificationToken myToken = new VerificationToken(token, user);
		tokenRepository.save(myToken);

	}

	@Override
	public VerificationToken getVerificationToken(String VerificationToken) {
		return tokenRepository.findByToken(VerificationToken);
	}

	@Override
	public AssetRequest findById(Long id) {
		return assetDao.findById(id);
	}

	@Override
	public List<AssetRequest> listAsset(User user) {
		String sql = "select r.email,r.assetname,r.assettype,r.reason,r.quantity,r.date from requestasset r where r.email = '"
				+ user.getEmail() + "'";
		List<AssetRequest> listAssets = jdbcTemplate.query(sql, new RowMapper<AssetRequest>() {

			@Override
			public AssetRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssetRequest asset = new AssetRequest();
			
				asset.setAssetname(rs.getString("assetname"));
				asset.setAssettype(rs.getString("assettype"));
				asset.setReason(rs.getString("reason"));
				asset.setRequestdate(rs.getString("date"));
				asset.setQuantity(rs.getString("quantity"));

				return asset;
			}
		});
		return listAssets;
	}

	@Override
	public List<User> listEmail() {
		String sql = "select email, firstname , lastname from user";
		List<User> listEmail = jdbcTemplate.query(sql, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();

				user.setEmail(rs.getString("email"));
				user.setFirstname(rs.getString("firstname"));
				user.setLastname(rs.getString("lastname"));
				return user;
			}
		});
		return listEmail;

	}

	@Override
	public List<AssetRequest> listAssetsRequest(HttpSession session) {

		String sql = "select assettype,assetname,quantity from requestasset  where date = CURDATE() && email = '"
				+ session.getAttribute("email") + "'";
		List<AssetRequest> listAssetsRequest = jdbcTemplate.query(sql, new RowMapper<AssetRequest>() {

			@Override
			public AssetRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssetRequest assetRequest = new AssetRequest();

				assetRequest.setAssettype(rs.getString("assettype"));
				assetRequest.setAssetname(rs.getString("assetname"));
				assetRequest.setQuantity(rs.getString("quantity"));
				return assetRequest;
			}
		});
		return listAssetsRequest;
	}
}
