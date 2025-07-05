package com.betabase.models;

public class CompositeMember {
    private User user;
    private Membership membership;

    public CompositeMember() {}

    public CompositeMember(User user, Membership membership) {
        this.user = user;
        this.membership = membership;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = membership; }

    // Convenience methods for common uses
    public String getFullName() {
        return user.getLastName() + ", " + user.getFirstName() +
            (user.getPrefName() != null && !user.getPrefName().isBlank()
             ? " (\"" + user.getPrefName() + "\")"
             : "");
    }

    public Long getId() { return user.getId(); }

    public boolean getChecked() { return membership.getChecked(); }
    public void setChecked(boolean checked) { membership.setChecked(checked); }

    public boolean isActive() { return membership.isActive(); }

    public boolean exists() { return user != null && membership != null; }
    
}

