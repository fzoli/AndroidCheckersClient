package org.dyndns.fzoli.mill.common.permission;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zoli
 */
public final class Permissions {
    
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
 *  - chatelés barátlistán kívüliekkel illetve azokkal, akiknél tiltva van a joggal rendelkező
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
    
    public static final Permission[] PERMISSIONS = Permission.values();
    public static final int MIN = 0, MAX = (int) Math.pow(2, PERMISSIONS.length) - 1, ROOT = -1;

    private Permissions() {
    }
    
    private static int getMask(Permission permission) {
        return (int) Math.pow(2, permission.ordinal());
    }
    
    public static int getMask(List<Permission> permissions) {
        int i = 0;
        List<Permission> tmp = new ArrayList<Permission>();
        for (Permission p : permissions) {
            if (tmp.contains(p)) continue;
            tmp.add(p);
            i += getMask(p);
        }
        return i;
    }
    
    public static boolean hasPermission(int mask, Permission permission) {
        if (mask == ROOT) return true;
        if (mask < MIN) return false;
        return (mask & getMask(permission)) != 0;
    }
    
    public static List<Permission> getPermissions(int mask) {
        List<Permission> ps = new ArrayList<Permission>();
        for (Permission p : PERMISSIONS) {
            if (hasPermission(mask, p)) ps.add(p);
        }
        return ps;
    }
    
    public static int incPermission(int mask, Permission permission) {
        if (!hasPermission(mask, permission)) return mask + getMask(permission);
        else return mask;
    }
    
    public static int decPermission(int mask, Permission permission) {
        if (hasPermission(mask, permission)) return mask - getMask(permission);
        else return mask;
    }
    
    private static void print(int i) {
        System.out.println(i + " = " + getMask(getPermissions(i)) + ": " + getPermissions(i));
    }
    
    public static void main(String[] args) {
        print(ROOT);
        for (int i = MIN; i <= MAX; i++) {
            print(i);
        }
    }
    
}