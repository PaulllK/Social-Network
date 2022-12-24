package com.example.socialnetwork.services;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repositories.FriendshipRepo;
import com.example.socialnetwork.repositories.UserRepo;
import com.example.socialnetwork.validators.FriendshipValidator;
import com.example.socialnetwork.validators.UserValidator;

import java.util.*;

public class UserService {

    private UserRepo userRepo;
    private FriendshipRepo frndRepo;
    private UserValidator val;
    private FriendshipValidator fVal;

    public UserService(UserRepo userRepo, FriendshipRepo frndRepo, UserValidator val, FriendshipValidator fVal) {
        this.userRepo = userRepo;
        this.frndRepo = frndRepo;
        this.val = val;
        this.fVal = fVal;
    }

    public FriendshipRepo getFrndRepo() {
        return frndRepo;
    }

    public void setFrndRepo(FriendshipRepo frndRepo) {
        this.frndRepo = frndRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserValidator getVal() {
        return val;
    }

    public void setVal(UserValidator val) {
        this.val = val;
    }

    public void addTheUser(String firstName, String lastName, String password) {
        User user = new User(firstName, lastName, password);
        val.validateUser(user);
        userRepo.add(user);
    }

    public List<User> allUsers() {
        return userRepo.getAllUsers();
    }

    public void deleteTheUser(int id) {
        frndRepo.deleteWithId(id);
        userRepo.delete(id);
    }

    private void possibleFriendship(int id1, int id2) {
        if(userRepo.userExists(id1) == false || userRepo.userExists(id2) == false)
            throw new RepoException("at least one of the users is non-existent!\n");
    }

    public List<User> makeFriends(int id1, int id2) {
        possibleFriendship(id1, id2);

        Friendship frnd = new Friendship(id1, id2);
        fVal.validateFriendship(frnd);
        frndRepo.add(frnd);

        return Arrays.asList(new User[] {userRepo.find(id1), userRepo.find(id2)});
    }

    public List<Friendship> allFrnds() {
        return frndRepo.getAll();
    }

    public List<User> deleteTheFriendship(int id) {
        List<User> usr = Arrays.asList(new User[] {userRepo.find(frndRepo.find(id).getId1()), userRepo.find(frndRepo.find(id).getId2())});
        frndRepo.delete(id);
        return usr;
    }

    private void dfs(List<Integer> viz, int vf, Map<Integer, List<Integer>> network) {
        viz.add(vf);

        List<Integer> frIds = network.get(vf);

        for(int i = 0; i < frIds.size(); i++)
            if (!viz.contains(frIds.get(i)))
                dfs(viz, frIds.get(i), network);
    }

    public int communities() {
        List<User> users = userRepo.getAllUsers();
        int size = users.size();

        // no users means 0 communities
        if(size == 0)
            return 0;

        // we will use neighbours (friends' id's) lists for each node (user)
        // ex. 2: 5 7 4
        //     5: 2 8
        //     7: 2
        //     4: 2 3
        //     3: 4
        Map<Integer, List<Integer>> network = new HashMap<Integer, List<Integer>>();

        // building friends' id's list for every user, in the hash map
        users.forEach(u -> {
            int i = 0, id = u.getId();
            List<Friendship> friendships = frndRepo.getAll();

            network.put(id, new ArrayList<Integer>()); // every user has its friends list (empty or not)

            while(i < friendships.size()) {

                int id1 = friendships.get(i).getId1(), id2 = friendships.get(i).getId2();

                if (id1 == u.getId()) {
                    // add id2
                    network.get(id).add(id2);
                }
                else if(id2 == u.getId()) {
                    // add id1
                    network.get(id).add(id1);
                }

                i++;
            }
        });

        List<Integer> viz = new ArrayList<Integer>(); // list of all visited nodes (users)

        // visit all graph vertices
        int nr = 0;

        for(User u : users) {
            if (!viz.contains(u.getId())) {
                nr++;
                dfs(viz, u.getId(), network);
            }
        }

        return nr;

    }
}
