# Session Authentication Debug Guide

## Problem
User can login but gets "Niste prijavljeni" error when creating wallets or savings goals.

## Root Cause
Session cookie not being sent with subsequent requests after login.

## Solution Applied

### 1. Created SessionConfig.java
**File**: `src/main/java/com/example/WalletApp/config/SessionConfig.java`

Configured cookie serializer with:
- `SameSite: Lax` (allows cross-origin for localhost)
- `UseSecureCookie: false` (for localhost development)
- `UseHttpOnlyCookie: true` (security)
- `DomainNamePattern: ^.+$` (allow any domain)

### 2. How to Test

**Step 1**: Restart Backend
```bash
cd /home/vuk/Downloads/WalletApp
mvn spring-boot:run
```

**Step 2**: Open Browser DevTools
- Network tab
- Check "Preserve log"

**Step 3**: Login
1. Go to http://localhost:5173/login
2. Login with credentials
3. Check Network → Headers → Response Headers
4. Should see: `Set-Cookie: JSESSIONID=...`

**Step 4**: Create Wallet
1. Go to Wallets page
2. Click "Nova novčanik"
3. Check Network → Headers → Request Headers
4. Should see: `Cookie: JSESSIONID=...`

### 3. Verify Session Cookie

**Browser Console**:
```javascript
// Check if cookie exists
document.cookie
```

Should see: `JSESSIONID=...`

### 4. Backend Logs

Check console for session info:
- Session creation during login
- Session validation on wallet/goal creation

### 5. Common Issues

**Issue 1**: No JSESSIONID cookie after login
- Check CORS allowCredentials: true
- Check axios withCredentials: true

**Issue 2**: Cookie exists but not sent
- Check cookie domain/path
- Check SameSite attribute

**Issue 3**: Session invalidated between requests
- Check session timeout
- Check if session store is working

### 6. Manual Test

**Postman/Curl**:
```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}' \
  -c cookies.txt

# 2. Create Wallet (should use session from cookies.txt)
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","initialBalance":1000,"currencyId":1,"savings":false}' \
  -b cookies.txt
```

## Expected Behavior After Fix

1. ✅ User logs in → JSESSIONID cookie set
2. ✅ Browser stores cookie
3. ✅ All subsequent requests include cookie
4. ✅ Backend validates session from cookie
5. ✅ User can create wallets/goals
