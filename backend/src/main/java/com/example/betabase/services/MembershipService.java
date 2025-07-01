@Service
public class MembershipService {
    private final MembershipRepository membershipRepo;
    private final UserRepository userRepo;
    private final GymRepository gymRepo;

    public MembershipService(MembershipRepository membershipRepo,
                             UserRepository userRepo,
                             GymRepository gymRepo) {
        this.membershipRepo = membershipRepo;
        this.userRepo = userRepo;
        this.gymRepo = gymRepo;
    }

    public Membership createMembership(MembershipCreateDto dto) {
        User user = userRepo.findByEmail(dto.getUserEmail())
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(dto.getUserEmail());
                newUser.setFirstName(dto.getFirstName());
                newUser.setLastName(dto.getLastName());
                newUser.setPhoneNumber(dto.getPhoneNumber());
                newUser.setDateOfBirth(dto.getDateOfBirth());
                return userRepo.save(newUser);
            });

        Gym gym = gymRepo.findById(dto.getGymId())
            .orElseThrow(() -> new RuntimeException("Gym not found"));

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setGym(gym);
        membership.setType(dto.getType());
        membership.setMemberSince(dto.getMemberSince());
        membership.setActive(true);

        return membershipRepo.save(membership);
    }

    public List<Membership> getMembershipsForGym(Long gymId) {
        return membershipRepo.findByGymId(gymId);
    }

    public List<Membership> getMembershipsForUser(Long userId) {
        return membershipRepo.findByUserId(userId);
    }

    public Optional<Membership> getById(Long id) {
        return membershipRepo.findById(id);
    }
}
