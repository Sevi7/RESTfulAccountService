Account service according to REST guidelines.

Access account:
curl -X GET localhost:8080/accounts/idAccountToAccess

Find accounts:
curl -X GET localhost:8080/accounts

Add account:
curl -X POST localhost:8080/accounts -H "Content-Type:application/json" -d "{\"name\": \"addName\", \"currency\":\"addCurrency\", \"balance\":addBalance,\"treasury\":trueOrfalse}"
-Only treasury accounts (Treasury property) can have a negative balance

Edit account:	
curl -X PUT localhost:8080/accounts/idAccoutToEdit -H "Content-Type:application/json" -d "{\"name\": \"editName\", \"currency\":\"editCurrency\", \"balance\":editBalance,\"treasury\":trueOrfalse}"
-Only treasury accounts (Treasury property) can have a negative balance
-Treasury property cannot be modified

Delete account:
curl -X DELETE localhost:8080/accounts/idAccountToDelete

Transfer money between accounts:
curl -X POST localhost:8080/transfer/idAccountDebited/idAccountCredited/money
-Treasury accounts cannot transfer money if they do not have enough balance.


Money transfer was decided to be done this way instead of creating an entity Transfer because it was not needed to have a record of the transfers.
Component testing was performed with AccountModelAssembler and AccountRepository at the same time, because AccountRepository is just an extension of JPARepository and has no code.