import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  email: String;
  password: String;

  constructor() { }

  ngOnInit() {
  }

  login(){
    console.log(this.email +" >< "+ this.password);

  }

}
