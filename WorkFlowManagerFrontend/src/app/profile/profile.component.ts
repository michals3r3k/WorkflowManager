import { Component, OnInit } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { ProfileEdit, ProfileService } from '../services/profile/profile.service';
import { Observable } from 'rxjs';
import { ServiceResultHelper } from '../services/utils/service-result-helper';
import { HttpRequestService } from '../services/http/http-request.service';
import { LoggedUserService } from '../services/login/logged-user.service';
import { LoginService } from '../services/login/login.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  editMode: boolean = false;
  imageUrl$: Observable<SafeUrl | null>;
  editData$: Observable<ProfileEdit>;
  newFile: FormData | null = null;

  connectedObjects: ProfileConnectedObjects;

  constructor(
    private service: ProfileService,
    private serviceResultHelper: ServiceResultHelper,
    private http: HttpRequestService,
    private loginService: LoginService,
  ) { 
    this.connectedObjects = {
      projects: [],
      organizations: [],
      tasks: [],
    }
  }

  ngOnInit() {
    this._loadImg();
    this._loadUserData();
    this._loadConnectedObjects();
  }

  _loadUserData() {
    this.editData$ = this.service.getData();
  }

  _loadImg() {
    this.imageUrl$ = this.service.getImg();
  }

  _loadConnectedObjects() {
    this.http.getGeneric<ProfileConnectedObjects>(`api/profile/connected-objects`).subscribe(connectedObjects => {
      this.connectedObjects = connectedObjects;
    });
  }

  toggleEditMode() {
    this.editMode = !this.editMode;
  }

  openImgUpload(event: Event) {
    const btn: HTMLElement = event.currentTarget as HTMLElement;
    const uploader = btn.querySelector("input[type='file']") as HTMLInputElement;
    if(uploader) {
      uploader.click();
    }
  }

  uploadImg(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if(file.type.startsWith('image/')) {
        this.newFile = new FormData();
        this.newFile.append("file", file);
        this.imageUrl$ = this.service.uploadImg(this.newFile);
      }
      else {
        alert('Please select image file')
      }
    }
  }

  save(editData: ProfileEdit) {
    this.service.saveData(editData).subscribe(result => {
      if(result.success) {
        this._loadUserData();
      }
      if(result.success && this.newFile) {
        this.saveAvatar();
      }
      else if(!this.newFile) {
        this.serviceResultHelper.handleServiceResult(result, "Profile data updated succesfully", "Errors occured");
      }
    });
  }

  saveAvatar() {
    if(this.newFile) {
      this.service.saveImg(this.newFile).subscribe(result => {
        this.serviceResultHelper.handleServiceResult(result, "Profile data updated succesfully", "Errors occured");
        if(result.success) {
          this._loadImg();
        }
      });
    }
  }

  logout() {
    this.loginService.logout();
  }

}

interface ProfileConnectedObjects {
  projects: ProjectRest[];
  organizations: OrganizationRest[];
  tasks: TaskRest[];
}

interface ProjectRest {
  organizationId: number;
  projectId: number;
  projectName: string;
  organizationName: string;
}

interface OrganizationRest {
  id: number;
  name: string;
}

interface TaskRest {
  taskId: number;
  organizationId: number;
  projectId: number;
  title: string;
  columnNameOrNull: string | null;
  organizationName: string;
  projectName: string;
}
