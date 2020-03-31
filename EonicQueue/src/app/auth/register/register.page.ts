import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {

  constructor(private router: Router,
    private activatedRoute: ActivatedRoute,
    private location: Location) { }

  ngOnInit() {
  }

  back()
	{
		this.location.back();
	}

}
