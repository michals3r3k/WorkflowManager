import { Component, ElementRef, HostListener, OnInit, Renderer2, ViewChild } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  @ViewChild('endOfMessages') endOfMessages: ElementRef | undefined;
  @ViewChild('messagesScroll') messagesScroll: ElementRef | undefined;

  private messagesScrollListener: (() => void) | undefined;

  message_text: string = "";

  messages: Message[] = []

  attachments_to_send :Attachment[] = []

  users = [
    {
      name: "user1"
    },
    {
      name: "user2"
    },
    {
      name: "user3"
    },
    {
      name: "user4"
    },
    {
      name: "user5"
    },
    {
      name: "user6"
    }
  ]

  attachments: Attachment[] = []

  constructor(private renderer: Renderer2) {
    var message1 = new Message()
    message1.sender = "Pan Danilecki";
    message1.own = false;
    message1.messa = "Zrobiliście już? >:C";
    message1.send_date = new Date('2024-08-05T12:00:00Z');

    var message2 = new Message()
    message2.sender = "Michał";
    message2.own = false;
    message2.messa = "A gdzie tam XDDD";
    message2.send_date = new Date('2024-08-07T14:00:00Z');

    this.messages.push(message1);
    this.messages.push(message2);
  }

  ngOnInit() {
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
  }

  sendMessage() {
    if (this.message_text.length === 0 && this.attachments_to_send. length === 0)
      return;

    let message = new Message();
    message.sender = "you";
    message.own = true;
    message.messa = this.message_text;
    message.attachments = this.attachments_to_send;

    this.messages.push(message)

    if (message.attachments.length > 0){
      Array.prototype.push.apply(this.attachments, message.attachments);
    }

    this.attachments_to_send = [];
    this.message_text = "";
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
      if (this.message_text.length === 0 && this.attachments_to_send. length === 0)
        return;
  
      let message = new Message();
      message.sender = "you";
      message.own = true;
      message.messa = this.message_text;
      message.attachments = this.attachments_to_send;
  
      this.messages.push(message)
  
      if (message.attachments.length > 0){
        Array.prototype.push.apply(this.attachments, message.attachments);
      }

      const textarea = document.querySelector('textarea');
      if (textarea) {
        textarea.value = "";
        this.message_text = "";
      }
  
      this.attachments_to_send = [];
      this.message_text = "";
      this.message_text = "";
    }
  }

  ClearText() {
    this.message_text = "";
  }

  removeAttachment(attach: Attachment) {
    this.attachments_to_send = this.attachments_to_send.filter(a => a !== attach);
  }

  onFileChanged(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      let attachment = new Attachment(file.name);
      const reader = new FileReader();
      const fileExtension = file.name.split('.').pop()?.toLowerCase() as string;

      if (['png', 'jpg', 'jpeg', 'gif'].includes(fileExtension)) {
        attachment.canBeDisplayed = true;
        reader.onload = () => {
          attachment.fileURL = reader.result;
        }
        reader.readAsDataURL(file);
      }
      else {
        attachment.canBeDisplayed = false;
        attachment.fileURL = null;
      }

      this.attachments_to_send.push(attachment);
    }
  }

}

class Attachment {
  name: string;
  file: File;
  extension: string;
  canBeDisplayed: boolean;
  fileURL: string | ArrayBuffer | null = null;

  public constructor(name: string){
    this.name = name;
    this.canBeDisplayed = false;
  }
}

class Message {
  sender: string;
  messa: string;
  own: boolean;
  send_date: Date = new Date();
  attachments: Attachment[];
}
