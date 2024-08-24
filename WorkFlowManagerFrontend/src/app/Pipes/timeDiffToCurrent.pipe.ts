import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeDiffToCurrent'
})
export class TimeDiffToCurrentPipe implements PipeTransform {

  transform(currentDate: Date | null, givenDate: Date): string {
    if(!currentDate) {
      return '';
    }
    const differenceInSeconds = Math.floor((currentDate.getTime() - givenDate.getTime()) / 1000);

    const intervals: { [key: string]: number } = {
      'year': 31536000,
      'month': 2592000,
      'week': 604800,
      'day': 86400,
      'hour': 3600,
      'minute': 60,
      'second': 1
    };

    let counter;
    for (const i in intervals) {
      counter = Math.floor(differenceInSeconds / intervals[i]);
      if (counter > 0) {
        return `${counter} ${i}${counter !== 1 ? 's' : ''} ago`;
      }
    }

    return 'just now';
  }

}
