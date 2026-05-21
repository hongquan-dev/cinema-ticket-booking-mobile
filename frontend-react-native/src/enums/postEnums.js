export const PostCategory = {
    NEWS: 'NEWS',
    PROMOTION: 'PROMOTION',
    FESTIVAL: 'FESTIVAL',
    ANNOUNCEMENT: 'ANNOUNCEMENT'
};

export const PostStatus = {
    DRAFT: 'DRAFT',
    PUBLISHED: 'PUBLISHED',
    HIDDEN: 'HIDDEN'
};

export const PostLabel = {
    [PostCategory.NEWS]: 'Tin tức',
    [PostCategory.PROMOTION]: 'Khuyến mãi',
    [PostCategory.FESTIVAL]: 'Sự kiện',
    [PostCategory.ANNOUNCEMENT]: 'Thông báo',

    [PostStatus.DRAFT]: 'Bản nháp',
    [PostStatus.PUBLISHED]: 'Công khai',
    [PostStatus.HIDDEN]: 'Đang ẩn'
};