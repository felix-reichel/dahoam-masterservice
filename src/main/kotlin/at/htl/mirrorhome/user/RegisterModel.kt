package at.htl.mirrorhome.user

class RegisterModel {
    var md5Picture: String = ""
    var firstname: String = ""
    var username: String = ""
    var password: String = ""

    fun update(obj: RegisterModel): RegisterModel = this.apply {
        firstname = obj.firstname;
        username = obj.username;
        password = obj.password;
    }
}