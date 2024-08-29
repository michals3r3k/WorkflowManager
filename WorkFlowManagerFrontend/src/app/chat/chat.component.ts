import { Component, ElementRef, HostListener, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { WebsocketService } from '../services/websocket/websocket.service';
import { interval, map, merge, Observable, Observer, of, startWith, Subject, Subscription } from 'rxjs';
import { LoggedUser, LoggedUserService } from '../services/login/logged-user.service';
import { HttpRequestService } from '../services/http/http-request.service';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  @ViewChild('endOfMessages') endOfMessages: ElementRef | undefined;
  @ViewChild('messagesScroll') messagesScroll: ElementRef | undefined;
  @Input() chatId: number;

  loggedUser: LoggedUser;
  users: UserModel[];

  messages: MessageModel[];
  message_text: string;
  messageAddedSubject = new Subject<void>();
  chatSubscription?: Subscription;

  attachments: AttachmentModel[];
  attachments_to_send :AttachmentModel[];
  currentDate$: Observable<Date>;

  private messagesScrollListener: (() => void) | undefined;

  constructor(private loggedUserService: LoggedUserService, 
    private http: HttpRequestService,
    private httpClient: HttpClient,
    private websocketService: WebsocketService) {
      this.users = [];
      this.messages = [];
      this.attachments = [];
      this.attachments_to_send = [];
      this.message_text = "";
  }

  ngOnInit() {
    this._initCurrentDate();
    this.loggedUserService.user$.subscribe(user => {
      if(user) {
        this.loggedUser = user;
        this._initMessages();
        this._initMessageWebsocket();
      }
    });
  }

  _initCurrentDate() {
    const timer$ = interval(30000).pipe(
      map(() => new Date())
    );
    const messageAdded$ = this.messageAddedSubject.pipe(
      map(() => new Date())
    );
    this.currentDate$ = merge(timer$, messageAdded$).pipe(
      startWith(new Date())
    );
  }

  _initMessageWebsocket() {
    this.websocketService.connect();
    this.websocketService.getConnectedObservable().subscribe(() => {
      this._subscribeStompMessages();
    });
  }

  _subscribeStompMessages() {
    this.websocketService.subscribe<MessageResponse>(`/topic/chat/${this.chatId}/messages`).subscribe(messageRes => {
      this._addMessage(messageRes);
      this.messageAddedSubject.next();
      this._registerNewUser(messageRes.creatorId, messageRes.creatorName);
    });
  }

  _initMessages() {
    this.http.getGeneric<MessageResponse[]>(`api/chat/${this.chatId}/init-chat`).subscribe(messagesRest => {
      messagesRest.forEach(messageRes => {
        this._addMessage(messageRes);
      });
      this._initUsers();
    });
  }

  _addMessage(messageRes: MessageResponse) {
    const message = new MessageModel();
    message.senderId = messageRes.creatorId;
    message.messa = messageRes.content;
    message.own = this.loggedUser.id == messageRes.creatorId;
    message.send_date = new Date(messageRes.createTime);
    message.sender = messageRes.creatorName;
    message.attachments = [];
    messageRes.files.forEach(fileRes => {
      const attachment = new AttachmentModel(fileRes.name);
      attachment.fileId = fileRes.id;
      this._setAttachmentUrl(attachment);
      message.attachments.push(attachment);
      this.attachments.push(attachment);
    });
    this.messages.push(message);
  }

  _registerNewUser(userId: number, name: string) {
    const exists = this.users.some(user => user.userId == userId);
    if(exists) {
      return; 
    }
    const user = new UserModel();
    user.userId = userId;
    user.name = name;
    this.users.push(user);
    this._initUser(user);
  }

  _initUsers() {
    this.http.getGeneric<UserRest[]>(`api/chat/${this.chatId}/users`).subscribe(usersRest => {
      usersRest.forEach(userRest => {
        const user = new UserModel();
        user.userId = userRest.id;
        user.name = userRest.name;
        this.users.push(user);
        this._initUser(user);
      });
    });
  }

  _initUser(user: UserModel) {
    if(!user.userId) {
      return;
    }
    const headers = this.http.getHttpHeaders()
    if(!headers) {
      return;
    }
    this.httpClient.get(`http://localhost:8080/api/chat/user/${user.userId}/img`, {headers: headers, responseType: 'blob'}).subscribe(blob => {
      const objectURL = !!blob.size ? URL.createObjectURL(blob) : null;
      user.imgUrl = objectURL;
    });
  }

  ngAfterViewInit() {
    if (this.endOfMessages) {
      this.endOfMessages.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }
    if (this.messagesScroll){
      this.messagesScrollListener = () => {
        const container = this.messagesScroll!.nativeElement as HTMLElement;
        if (container.scrollTop === 0){
          // Miejsca na obsługę łądowania dynamicznego wiadomości
          console.log("XD")
        }
      };
      this.messagesScroll.nativeElement.addEventListener('scroll', this.messagesScrollListener);
    }
  }

  ngOnDestroy(): void {
    if (this.messagesScroll && this.messagesScrollListener) {
      this.messagesScroll.nativeElement.removeEventListener('scroll', this.messagesScrollListener);
    }
    this.chatSubscription?.unsubscribe();
  }

  sendMessageArea(event: KeyboardEvent) {
    if (event.key === 'Enter' && event.shiftKey){
      event.preventDefault();
      const textarea = document.querySelector('textarea');
      if (textarea) {
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        this.message_text = this.message_text.substring(0, start) + '\n' + this.message_text.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 1;
      }
    }
    else if (event.key === 'Enter') {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage() {
    if (this.message_text.length === 0 && this.attachments_to_send. length === 0) {
      return;
    }
    this.uploadFile(this.attachments_to_send[0]).subscribe(messageIdOrNull => {
      const messageRequest: MessageRequestWs = {userId: this.loggedUser.id, message: this.message_text, messageId: messageIdOrNull};
      this.websocketService.send(`/app/chat/${this.chatId}/send-message`, messageRequest);
      this.attachments_to_send = [];
      this.message_text = "";
    });
  }

  uploadFile(attachment: AttachmentModel): Observable<number | null> {
    if(!attachment || !attachment.file) {
      return of(null);
    }
    const headers = this.http.getHttpHeaders();
    if(!headers) {
      return of(null);
    }
    const file: File = attachment.file;
    const formData = new FormData();
    formData.append("file", file);
    return this.httpClient.post<number | null>(`http://localhost:8080/api/chat/${this.chatId}/file/upload`, formData, {headers: headers});
  }

  removeAttachment(attach: AttachmentModel) {
    this.attachments_to_send = this.attachments_to_send.filter(a => a !== attach);
  }

  onFileChanged(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      let attachment = new AttachmentModel(file.name);
      attachment.file = file;
      this._setAttachmentUrl(attachment);
      this.attachments_to_send.push(attachment);
    }
  }

  _setAttachmentUrl(attachment: AttachmentModel) {
    attachment.fileURL = null;
    if (!this._isImage(attachment)) {
      return;
    }
    if(attachment.fileId) {
      const headers = this.http.getHttpHeaders()
      if(headers) {
        this.httpClient.get(`http://localhost:8080/api/chat/file/${attachment.fileId}/img`, {headers: headers, responseType: 'blob'}).subscribe(blob => {
          const objectURL = URL.createObjectURL(blob);
          attachment.fileURL = objectURL;//this.sanitizer.bypassSecurityTrustUrl(objectURL);
        });
      }
      return;
    }
    if(attachment.file) {
      const reader = new FileReader();
      reader.onload = () => {
        attachment.fileURL = reader.result;
      }
      reader.readAsDataURL(attachment.file);
    }
  }

  _isImage(attachment: AttachmentModel): boolean {
    const extension = attachment.name.split('.').pop()?.toLowerCase() as string;
    return ['png', 'jpg', 'jpeg', 'gif'].includes(extension);
  }

  downloadFile(attachment: AttachmentModel) {
    const headers = this.http.getHttpHeaders();
    if(!headers) {
      return;
    }
    headers.set('Accept', 'application/octet-stream');
    return this.httpClient.get(`http://localhost:8080/api/chat/file/${attachment.fileId}/download`, {headers: headers, responseType: 'blob'}).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = attachment.name; 
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    });
  }

  getSenderImgUrl(message: MessageModel): string | null {
    return this.users.filter(user => user.userId == message.senderId)[0].imgUrl;
  }

}

class UserModel {
  userId: number;
  name: string;
  imgUrl: string | null;
}

interface UserRest {
  id: number,
  name: string,
}

class AttachmentModel {
  name: string;
  file: File;
  fileURL: string | ArrayBuffer | null = null;
  fileId: number | null;

  public constructor(name: string){
    this.name = name;
  }
}

class MessageModel {
  senderId: number;
  sender: string;
  messa: string;
  own: boolean;
  send_date: Date;
  attachments: AttachmentModel[];
}

interface MessageRequestWs {
  userId: number,
  messageId: number | null,
  message: string,
}

interface MessageResponse {
  creatorId: number,
  creatorName: string,
  content: string,
  createTime: string,
  files: FileResponse[]
}

interface FileResponse {
  id: number,
  name: string,
}
