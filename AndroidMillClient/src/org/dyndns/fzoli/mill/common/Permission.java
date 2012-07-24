package org.dyndns.fzoli.mill.common;

import java.util.ArrayList;
import java.util.List;

/** Kuka...
 *  Egy adott jog gyakorlásának alap feltétele, hogy a maszk adjon rá jogot.
 *  A három kiegészítő alapelv:
 *  a) Egy adott jog akkor gyakorolható egy másik felhasználón, ha
 *     a másik felhasználó maszkja kisebb (egyenlőség nem megengedett).
 *     (Ebből adódik, hogy fontos a jogok felsorolásának sorrendje.)
 *  b) A felhasználó nem tud olyan jogot adni/elvenni, amivel ő nem rendelkezik
 *     még akkor sem, ha van joga a maszk szerkesztésére.
 *  c) Kitüntetett szerepű maszk a -1.
 *     - Ezen maszkkal az összes jog gyakorolható.
 *     - A legnagyobb maszknak számít.
 *     - A programban soha nem adható senkinek és nem vehető el senkitől.
 */
    
/** Alap jogkezelési gondolat.
 * 
 * Minden jog aktiválható/inaktiválható, ha birtokolja valaki, kivéve a védelem jogát, ami mindig aktív.
 * 
 *  Jogok:
 * 
 *  - statisztika elrejtése: nem láthatják mások, hogy mennyit nyert és veszített a felhasználó a dámában
 * 
 *  - láthatatlan státusz észlelése: annak ellenére, hogy a felhasználó láthatatlan,
 *    ezzel a joggal lehet látni, ha online
 * 
 *  - láthatatlanság nyilvántartásban: a nyilvántartásból elrejtheti magát a felhasználó.
 *    az elrejtés nem érvényes a barátlistára.
 *  
 *  - inaktív felhasználók listázása, adataiknak olvasása
 * 
 *  - chatelés barátlistán kívüliekkel illetve azokkal, akiknél tiltva van a joggal rendelkező -> olyan rendszerüzenet küldése, ami csak 1 embernél, a címzettnél jelenik meg
 * 
 *  - rendszerüzenet küldés: mindenki számára azonnal felugró üzenet küldése
 * 
 *  - felhasználó bannolása: képesség felhasználó bannolására
 *    (bannolás: felhasználó bejelentkezésének tiltása és ha online, azonnali kijelentkeztetése)
 * 
 *  - felhasználó törlése (felfüggesztése): képesség felhasználó törlésére
 *    (törlés: szem. adatok nullázása, felhasználó státusz inaktívvá állítás ami elrejti a nyilvántartásból őt)
 * 
 *  - jogok szerkesztése: képesség jog adására és elvételére
 * 
 *  - védelem: a jogot birtokló felhasználón nem tud senki jogot gyakorolni.
 *             Ez a jog a többivel ellentétben nem aktiválható/inaktiválható.
 *             pl. nem törölheti; láthatatlanságát nem tudja detektálni,
 *                 ha rejtve van a nyilvántartásban és van joga észleléshez, akkor sem látja;
 *                 nem szerkesztheti jogait; nem tudja elrejteni előtte a statisztikáját;
 *                 nem tudja detektálni ha elrejtette magát
 */ 
/**   Kitüntetett szerepű maszk a -1.
 *    - Ezen maszkkal az összes jog aktív, tehát nem szerkeszthetők a jogai a felhasználónak.
 *    - A védelem joga nem korlátozza és csak ő adhatja illetve veheti el ezt a jogot.
 *    - A programon belül soha nem adható senkinek és nem vehető el senkitől.
 *    - A programon belül nem tudja saját magát kitörölni ellentétben a többiekkel.
 */

/**
 *
 * @author zoli
 */
public enum Permission {
    
    HIDDEN_STATISTICS(Group.STATE_INVERSE),
    DETECT_INVISIBLE_STATUS(Group.STATE_NORMAL),
    DETECT_SUSPENDED_PLAYER(Group.STATE_NORMAL),
//    HIDDEN_PLAYER_DETECT(Group.STATE_NORMAL),
//    SHOW_ALL_PERSONAL_DATA(Group.STATE_NORMAL),
    HIDDEN_MODE(Group.STATE_INVERSE),
//    SEE_EVERYONES_AVATAR(Group.STATE_NORMAL),
//    CHAT_EVERYONE(Group.STATE_NORMAL),
    SEND_PERSON_MESSAGE(Group.SYSTEM),
    SEND_SYSTEM_MESSAGE(Group.SYSTEM),
    PLAYER_BANN(Group.TARGET),
    PLAYER_SUSPEND(Group.TARGET),
    PERMISSION_EDIT(Group.TARGET),
    SHIELD_MODE(Group.STATE_INVERSE);
    
    public static final int ROOT = -1;
    private static final int MIN = 0, MAX = (int) Math.pow(2, Permission.values().length) - 1;
    
    public static enum Group {
        TARGET, STATE_NORMAL, STATE_INVERSE, SYSTEM
    };
    
    private final Group GROUP;
    
    private Permission(Group group) {
        GROUP = group;
    }
    
    public Group getGroup() {
        return GROUP;
    }
    
    public int getMask() {
        return getMask(this);
    }
    
    public boolean hasPermission(int mask) {
        return hasPermission(mask, this);
    }
    
    public boolean hasAllPermission() {
        return hasAllPermission(getMask());
    }
    
    public int incPermission(int mask) {
        return incPermission(mask, this);
    }
    
    public int decPermission(int mask) {
        return decPermission(mask, this);
    }
    
    public static int getMask(Permission p) {
        return (int) Math.pow(2, p.ordinal());
    }
    
    public static int getMask(List<Permission> permissions) {
        int i = 0;
        List<Permission> tmp = new ArrayList<Permission>();
        for (Permission p : permissions) {
            if (tmp.contains(p)) continue;
            tmp.add(p);
            i += p.getMask();
        }
        return i;
    }
    
    public static List<Permission> getPermissions(int mask) {
        List<Permission> ps = new ArrayList<Permission>();
        for (Permission p : Permission.values()) {
            if (p.hasPermission(mask)) ps.add(p);
        }
        return ps;
    }
    
    public static boolean hasPermission(int mask, Permission p) {
        if (mask == ROOT) return true;
        if (mask < MIN || mask > MAX) return false;
        return (mask & getMask(p)) != 0;
    }
    
    public static boolean hasAllPermission(int mask) {
        if (mask == ROOT) return true;
        return mask == MAX;
    }
    
    public static int incPermission(int mask, Permission p) {
        if (!hasPermission(mask, p)) return mask + getMask(p);
        else return mask;
    }
    
    public static int decPermission(int mask, Permission p) {
        if (hasPermission(mask, p)) return mask - getMask(p);
        else return mask;
    }
    
}