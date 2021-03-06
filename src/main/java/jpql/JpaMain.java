package jpql;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.setTeam(teamA);
            member1.setType(MemberType.ADMIN);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(10);
            member2.setTeam(teamA);
            member2.setType(MemberType.ADMIN);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(10);
            member3.setTeam(teamB);
            member3.setType(MemberType.ADMIN);
            em.persist(member3);

            em.flush();
            em.clear();

            String entityQuery = "select m from Member m where m = :member";
            Member entity = em.createQuery(entityQuery, Member.class)
                    .setParameter("member", member1)
                    .getSingleResult();

            System.out.println("entity = " + entity);

            em.flush();
            em.clear();

            String entityIdQuery = "select m from Member m where m.id = :memberId";
            Member entityId = em.createQuery(entityIdQuery, Member.class)
                    .setParameter("memberId", member1.getId())
                    .getSingleResult();

            System.out.println("entityId = " + entityId);

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void fetchJoin() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.setTeam(teamA);
            member1.setType(MemberType.ADMIN);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(10);
            member2.setTeam(teamA);
            member2.setType(MemberType.ADMIN);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(10);
            member3.setTeam(teamB);
            member3.setType(MemberType.ADMIN);
            em.persist(member3);

            em.flush();
            em.clear();

//            String query = "select m from Member m";
            // fetch join ??? ???????????? FetchType??? Lazy ??? ??? join?????? ?????? ????????? (Eager??? ?????? ????????? ??????????????? n+1 ?????? ??????)
//            String query = "select m from Member m join fetch m.team";
//
//            List<Member> result = em.createQuery(query, Member.class).getResultList();
//            for (Member member : result) {
//                System.out.println("member = " + member.getUsername() +" , " + member.getTeam().getName());
//            }
//
//            em.flush();
//            em.clear();

            // ????????? ?????? ????????????, ???????????? ?????????
            String teamQuery = "select t from Team t";
            List<Team> teamList = em.createQuery(teamQuery, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

            // ???A??? ???????????? ??? row??? ????????? (????????? 2????????????..) ?????? ?????? ??? ?????? ???????????? ???????????????..
            for (Team team : teamList) {
                System.out.println("** team.getName() = " + team.getName() + "|" + team.getMembers().size());
                for ( Member member : team.getMembers()) {
                    System.out.println(" --> member = " + member);
                }
            }

            em.flush();
            em.clear();

//            String distinctQuery = "select distinct t from Team t join fetch t.members";
//            List<Team> distinctList = em.createQuery(distinctQuery, Team.class).getResultList();
//
//            // JPQL??? DISTINCT??? ???????????? ????????? ????????? ????????????!
//            for (Team team : distinctList) {
//                System.out.println("distinct team.getName() = " + team.getName() + "|" + team.getMembers().size());
//                for ( Member member : team.getMembers()) {
//                    System.out.println(" --> member = " + member);
//                }
//            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void joinQuery() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(teamA);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member memberTeamA = new Member();
//            memberTeamA.setUsername("teamA");
            memberTeamA.setAge(60);
            memberTeamA.setTeam(teamA);
            memberTeamA.setType(MemberType.USER);
            em.persist(memberTeamA);

            // ???????????? ?????? ???... ????????? ????????? ?????? ????????? ????????? ??????
            String query = "select t.members from Team t";
            Collection result = em.createQuery(query, Collection.class).getResultList();

            for (Object o : result) {
                System.out.println("o = " + o);
            }

            String explicitJoin = "select m.username from Member m join m.team t";
            List<String> explicitJoinResult = em.createQuery(explicitJoin, String.class).getResultList();
            for (String s : explicitJoinResult) {
                System.out.println("s = " + s);
            }

            String sizeQuery = "select t.members.size from Team t";
            Integer sizeResult = em.createQuery(sizeQuery, Integer.class).getSingleResult();
            System.out.println("sizeResult = " + sizeResult);

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void myFunction() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member memberTeamA = new Member();
//            memberTeamA.setUsername("teamA");
            memberTeamA.setAge(60);
            memberTeamA.setTeam(team);
            memberTeamA.setType(MemberType.USER);
            em.persist(memberTeamA);

//            String query = "select 'a' || 'b' from Member m";
            String query = "select concat('a','b') from Member m";
            List<String> result = em.createQuery(query, String.class).getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }

            String locateQuery = "select locate('1', m.username) from Member m";
            List<Integer> locateResult = em.createQuery(locateQuery, Integer.class).getResultList();
            for (Integer integer : locateResult) {
                System.out.println("integer = " + integer);
            }

            String myFunctionQuery = "select function('group_concat', m.age) from Member m ";
            List<String> myFunctionResult = em.createQuery(myFunctionQuery, String.class).getResultList();
            for (String s : myFunctionResult) {
                System.out.println("s = " + s);
            }


            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void caseAndCoalesceAndNullIf() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member memberTeamA = new Member();
//            memberTeamA.setUsername("teamA");
            memberTeamA.setAge(60);
            memberTeamA.setTeam(team);
            memberTeamA.setType(MemberType.USER);
            em.persist(memberTeamA);

            // ?????? CASE ???
            String query =
                    "select " +
                            "case when m.age <= 10 then '????????????' " +
                            "     when m.age >= 60 then '????????????' " +
                            "     else '????????????' " +
                            "end "+
                            "from Member m";

            List<String> result = em.createQuery(query, String.class).getResultList();
            for (String fee : result) {
                System.out.println("?????? = " + fee);
            }

            // coalesce - null ?????? ??? ?????? ???????????? ??????, null ??? ????????? username ??????
            String coalesceQuery = "select coalesce(m.username, '?????? ?????? ??????') as username " +
                    "from Member m";

            List<String> coalesceResult = em.createQuery(coalesceQuery, String.class).getResultList();
            for (String username : coalesceResult) {
                System.out.println("username = " + username);
            }

            // nullif - ??? ?????? ??????????????? ?????? ????????? null ??????
            String nullifQuery = "select nullif(m.username, 'member1') as username " +
                    "from Member m";
            List<String> nullifResult = em.createQuery(nullifQuery, String.class).getResultList();
            for (String username : nullifResult) {
                System.out.println("username = " + username);
            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void jpqlType() {
//        String query = "select m.username, 'HELLO', TRUE from Member m" +
//                " where m.type = :memberType";
////                            " where m.type = jpql.MemberType.ADMIN";
//        List<Object[]> resultList = em.createQuery(query)
//                .setParameter("memberType", MemberType.ADMIN)
//                .getResultList();
//        for (Object[] o : resultList) {
//            System.out.println("o[0] = " + o[0]);
//            System.out.println("o[1] = " + o[1]);
//            System.out.println("o[2] = " + o[2]);
//        }
//
//        // type?????? ??????(DType)
////            em.createQuery("select i from Item i where type(i) = Book", Item.class).getResultList();

    }

    private void subquery() {

//        // JPA??? WHERE, HAVING ???????????? ?????? ?????? ?????? ??????, FROM??? ???????????? ??? ???!
//        String selectSubquery = "select (select avg(m1.age) from Member m1) as avgAge from Member m";
//        List<Double> result = em.createQuery(selectSubquery, Double.class)
//                .getResultList();
//        for (Double age : result) {
//            System.out.println(age);
//        }
    }

    private void paging() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            for(int i=0; i<100; i++) {
                Member member = new Member();
                member.setUsername("member"+i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result.size() = " + result.size());
            for (Member m : result) {
                System.out.println("m = " + m);
            }


            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void projection() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

//            Member member2 = new Member();
//            member.setUsername("member2");
//            em.persist(member2);

            em.flush();
            em.clear();

            // Tuple
            List<Tuple> tupleList = em.createQuery("select m.username as username, m.age as age from Member m", Tuple.class).getResultList();
            for (Tuple tuple : tupleList) {
                String username = (String) tuple.get(0);
                int age = (int) tuple.get(1);
                String usernameByAlias = tuple.get("username", String.class);
                int ageByAlias = tuple.get("age", Integer.class);
            }

            // ????????? ?????? ????????????
            List nameAgeObjectList = em.createQuery("select distinct m.username, m.age from Member m").getResultList();
            for (Object o : nameAgeObjectList) {
                Object[] item = (Object[]) o;
                System.out.println(item[0]+","+item[1]);
                String username = (String) item[0];
                int age = (int) item[1];
            }

            List<Object[]> nameAgeObjectArrList = em.createQuery("select distinct m.username, m.age from Member m").getResultList();
            for (Object[] objects : nameAgeObjectArrList) {
                System.out.println(objects[0]+","+objects[1]);
                String username = (String) objects[0];
                int age = (int) objects[1];
            }

            List<MemberDTO> nameAgeDTOList = em.createQuery("select distinct new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
            for (MemberDTO memberDTO : nameAgeDTOList) {
                String username = memberDTO.getUsername();
                int age = memberDTO.getAge();
                System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
                System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
            }

            // ???????????? ????????????
            em.createQuery("select o.address from Order o", Address.class).getResultList();

            // ????????? ????????????
            // ??????????????? join ??? ????????? ????????? ?????????????????? ??????
            List<Team> teamsExplicitJoin = em.createQuery("select t from Member m join m.team t", Team.class).getResultList();
            List<Team> teamsImplicitJoin = em.createQuery("select m.team from Member m", Team.class).getResultList();

            // ????????? ????????????
            // entity ?????????????????? select ?????? ??????????????? ????????? ??????????????? ????????? ??????
            List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();

            Member findMember = result.get(0);
            // ???????????? ?????? ?????????
            findMember.setAge(20);

            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            // ?????? ?????? - ????????? ????????? ??????, ????????? ????????? NoResultException, ??? ?????? NonUniqueResultException
            Member singleResult = query1.getSingleResult();

            // ?????? ????????? - ????????? ????????? ??? ????????? ??????
            List<Member> resultList = query1.getResultList();
            for (Member member1 : resultList) {

            }

            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member m");

            // ?????? ?????? ???????????? ?????????, ????????? ????????? ????????? ??????
            TypedQuery<Member> byUsernameQuery = em.createQuery("select m from Member m where m.username =:username", Member.class);
            byUsernameQuery.setParameter("username", "member1");
            Member singleResultByUsername = byUsernameQuery.getSingleResult();
            System.out.println("singleResultByUsername = " + singleResultByUsername.getUsername());

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();;
    }
}
