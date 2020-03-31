export class Customer {

    customerId: number;
    firstName: String;
    lastName: String;
    email: String;
    password: String;
    salt: String;

    constructor(customerId?: number, firstName?: String, lastName?: String, email?: String, password?: String, salt?: String){

        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.salt = salt;

    }

}
