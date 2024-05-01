import { Component } from '@angular/core';
import { RegisterService } from '../services/login/register.service';
import { LoginService } from '../services/login/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  isSignDivVisiable: boolean  = true;

  signUpObj: SignUpModel  = new SignUpModel();
  loginObj: LoginModel  = new LoginModel();

  constructor(private loginService: LoginService, private registerService: RegisterService) {
    // itentionally empty
  }


  onRegister() {
    this.registerService.register(this.signUpObj.email, this.signUpObj.password);
  }

  onLogin() {
    this.loginService.login(this.loginObj.email, this.loginObj.password);
  }

}

export class SignUpModel  {
  name: string;
  email: string;
  password: string;

  constructor() {
    this.email = "";
    this.name = "";
    this.password= ""
  }
}

export class LoginModel  { 
  email: string;
  password: string;

  constructor() {
    this.email = ""; 
    this.password= ""
  }
}