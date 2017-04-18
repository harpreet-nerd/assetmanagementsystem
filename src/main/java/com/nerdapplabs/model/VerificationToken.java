package com.nerdapplabs.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

@Entity
public class VerificationToken {
	
	private static final int expiration = 60 * 24;
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;

	    private String token;

	    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	    @JoinColumn(nullable = false, name = "email", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
	    private User user;

	    private Date expiryDate;
	    
	    public VerificationToken() {
	        super();
	    }
	    
	    public VerificationToken(final String token, final User user) {
	        super();

	        this.token = token;
	        this.user = user;
	        this.expiryDate = calculateExpiryDate(expiration);
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Date getExpiryDate() {
			return expiryDate;
		}

		public void setExpiryDate(Date expiryDate) {
			this.expiryDate = expiryDate;
		}
	    
		private Date calculateExpiryDate(final int expiryTimeInMinutes) {
	        final Calendar cal = Calendar.getInstance();
	        cal.setTimeInMillis(new Date().getTime());
	        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
	        return new Date(cal.getTime().getTime());
	    }


}
